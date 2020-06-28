import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:nearby_mobility/models/ryde_response.dart';
import 'package:nearby_mobility/repository.dart';
import 'package:wear/wear.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

// Centered on the Stavanger/Sandnes area
const initialPosition = CameraPosition(
  target: LatLng(58.9109397, 5.7244898),
  zoom: 11.5,
);

class _MyAppState extends State<MyApp> {
  final _controller = Completer<GoogleMapController>();
  Future<RydeResponse> futureScooters;
  bool myLocationEnabled = false;

  @override
  void initState() {
    super.initState();
    futureScooters = fetchScooters();
    _initCurrentLocation();
  }

  @override
  Widget build(BuildContext context) {
    return AmbientMode(
      builder: (context, mode) {
        return MaterialApp(
          title: 'Nearby Mobility',
          theme: ThemeData(
            primarySwatch: Colors.blue,
            visualDensity: VisualDensity.adaptivePlatformDensity,
          ),
          darkTheme: ThemeData(
            brightness: Brightness.dark,
            scaffoldBackgroundColor: Colors.black,
            visualDensity: VisualDensity.adaptivePlatformDensity,
          ),
          themeMode: mode == Mode.active ? ThemeMode.light : ThemeMode.dark,
          home: Scaffold(
            body: GoogleMap(
              initialCameraPosition: initialPosition,
              myLocationEnabled: myLocationEnabled,
              onMapCreated: (GoogleMapController controller) {
                _controller.complete(controller);
              },
            ),
          ),
        );
      },
    );
  }

  _initCurrentLocation() async {
    try {
      final geolocator = Geolocator();
      var geolocationStatus =
          await geolocator.checkGeolocationPermissionStatus();
      bool granted = geolocationStatus == GeolocationStatus.granted;
      if (mounted) {
        setState(() {
          myLocationEnabled = granted;
        });
      }
      if (granted) {
        final position = await geolocator.getCurrentPosition();
        final controller = await _controller.future;
        if (mounted) {
          controller.animateCamera(CameraUpdate.newLatLngZoom(
              LatLng(position.latitude, position.longitude), 16));
        }
      }
    } on PlatformException {}
  }
}
