import 'dart:async';
import 'dart:developer' as developer;
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:nearby_mobility/marker_generator.dart';
import 'package:nearby_mobility/marker_widget.dart';
import 'package:nearby_mobility/models/scooter.dart';
import 'package:nearby_mobility/repository.dart';

class MapPage extends StatefulWidget {
  @override
  _MapPageState createState() => _MapPageState();
}

// Centered on the Stavanger/Sandnes area
const initialPosition = CameraPosition(
  target: LatLng(58.9109397, 5.7244898),
  zoom: 11.5,
);

class _MapPageState extends State<MapPage> {
  final _controller = Completer<GoogleMapController>();
  bool myLocationEnabled = false;
  Set<Marker> markers = Set();

  @override
  void initState() {
    super.initState();
    fetchScooters().then((value) {
      MarkerGenerator([MarkerWidget()], (bitmaps) {
        developer.log('Got bitmaps');
        if (mounted) {
          setState(() {
            markers = value.scooters
                .map((scooter) => _bitmapToMarker(bitmaps[0], scooter))
                .toSet();
          });
        }
      }).generate(context);
    }).catchError((error) =>
        developer.log('Failed to show scooters', error: error.toString()));
    _initCurrentLocation();
  }

  @override
  Widget build(BuildContext context) {
    return GoogleMap(
      initialCameraPosition: initialPosition,
      myLocationEnabled: myLocationEnabled,
      onMapCreated: (GoogleMapController controller) {
        _controller.complete(controller);
      },
      markers: markers,
    );
  }

  _initCurrentLocation() async {
    try {
      final geolocator = Geolocator();
      final position = await geolocator.getCurrentPosition();
      final controller = await _controller.future;
      if (mounted) {
        controller.animateCamera(CameraUpdate.newLatLngZoom(
          LatLng(position.latitude, position.longitude),
          15,
        ));

        var geolocationStatus =
            await geolocator.checkGeolocationPermissionStatus();
        if (mounted) {
          setState(() {
            myLocationEnabled = geolocationStatus == GeolocationStatus.granted;
          });
        }
      }
    } on PlatformException catch (error) {
      developer.log("Couldn't get location", error: error);
    }
  }

  Marker _bitmapToMarker(Uint8List bitmap, Scooter scooter) {
    return Marker(
        markerId: MarkerId(scooter.memberByString),
        position:
            LatLng(scooter.coordinate.latitude, scooter.coordinate.longitude),
        icon: BitmapDescriptor.fromBytes(bitmap),
        anchor: Offset(0.5, 0.5));
  }
}
