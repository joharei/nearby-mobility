import 'dart:async';
import 'dart:convert';
import 'dart:developer' as developer;

import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:http/http.dart' as http;
import 'package:nearby_mobility/env.dart';
import 'package:nearby_mobility/models/ryde_response.dart';
import 'package:nearby_mobility/models/scooter.dart';
import 'package:rxdart/rxdart.dart';

Future<List<Scooter>> get nearbyScooters async {
  final position = await Geolocator().getLastKnownPosition(
      locationPermissionLevel: GeolocationPermission.locationWhenInUse);

  return (await _fetchScooters(
          LatLng(position.latitude, position.longitude), 1))
      .scooters;
}

Future<RydeResponse> _fetchScootersFromBounds(LatLngBounds bounds) async {
  final center = LatLng(
    (bounds.southwest.latitude + bounds.northeast.latitude) / 2,
    (bounds.southwest.longitude + bounds.northeast.longitude) / 2,
  );
  final radius = (await Geolocator().distanceBetween(
        center.latitude,
        bounds.southwest.longitude,
        center.latitude,
        bounds.northeast.longitude,
      )) /
      1000;

  return await _fetchScooters(center, radius);
}

Future<RydeResponse> _fetchScooters(LatLng center, double radius) async {
  final response = await http.post(
    '$rydeBaseUrl/appRyde/getNearScooters',
    body: {
      'iotLa': center.latitude.toStringAsFixed(6),
      'iotLo': center.longitude.toStringAsFixed(6),
      'nearRadius': radius.toStringAsFixed(2),
      'cityId': '7',
    },
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
  );

  if (response.statusCode == 200) {
    return RydeResponse.fromJson(json.decode(response.body));
  } else {
    developer.log('Failed to load scooters', error: response.reasonPhrase);
    throw Exception('Failed to load scooters');
  }
}

Subject<LatLngBounds> visibleRegion = PublishSubject();

Stream<List<Scooter>> get scootersStream => Rx.combineLatest2(
      visibleRegion,
      Stream.periodic(Duration(minutes: 1)).startWith(null),
      (LatLngBounds bounds, _) => bounds,
    ).asyncMap(_fetchScootersFromBounds).map((response) => response.scooters);
