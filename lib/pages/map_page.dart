import 'dart:async';
import 'dart:typed_data';

import 'package:fluster/fluster.dart';
import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:nearby_mobility/marker_tools/clustering.dart' as Clustering;
import 'package:nearby_mobility/marker_tools/map_marker.dart';
import 'package:nearby_mobility/marker_tools/marker_generator.dart';
import 'package:nearby_mobility/marker_tools/marker_widget.dart';
import 'package:nearby_mobility/models/scooter.dart';
import 'package:nearby_mobility/repository.dart' as Repository;

class MapPage extends StatefulWidget {
  final CameraPosition initialCameraPosition;

  MapPage({Key key, @required Position initialPosition})
      : initialCameraPosition = initialPosition == null
            ? null
            : CameraPosition(
                target:
                    LatLng(initialPosition.latitude, initialPosition.longitude),
                zoom: 15,
              ),
        super(key: key);

  @override
  _MapPageState createState() => _MapPageState();
}

// Centered on the Stavanger/Sandnes area
const defaultPosition = CameraPosition(
  target: LatLng(58.9109397, 5.7244898),
  zoom: 11.5,
);

class _MapPageState extends State<MapPage> {
  final _controller = Completer<GoogleMapController>();
  Uint8List scooterMarkerIcon;
  Fluster<MapMarker> _clusterManager;
  double _currentZoom = 15;

  @override
  void initState() {
    super.initState();
    MarkerGenerator([MarkerWidget()], (bitmaps) {
      if (mounted) {
        setState(() {
          scooterMarkerIcon = bitmaps[0];
        });
      }
    }).generate(context);
  }

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<Set<Marker>>(
        stream: Repository.scootersStream.map((scooters) => scooters
            .map((scooter) => _bitmapToMarker(scooterMarkerIcon, scooter))
            .toSet()),
        builder: (context, snapshot) {
          return GoogleMap(
            initialCameraPosition:
                widget.initialCameraPosition ?? defaultPosition,
            myLocationEnabled: widget.initialCameraPosition != null,
            onMapCreated: (GoogleMapController controller) {
              _controller.complete(controller);
            },
            markers: snapshot.data,
            padding: EdgeInsets.all(8),
            onCameraIdle: () {
              _controller.future
                  .then((controller) => controller.getVisibleRegion())
                  .then(Repository.visibleRegion.add);
            },
          );
        });
  }

  Marker _bitmapToMarker(Uint8List bitmap, Scooter scooter) {
    return Marker(
        markerId: MarkerId(scooter.memberByString),
        position:
            LatLng(scooter.coordinate.latitude, scooter.coordinate.longitude),
        icon: BitmapDescriptor.fromBytes(bitmap),
        anchor: Offset(0.5, 0.5));
  }

  /// Gets the markers and clusters to be displayed on the map for the current zoom level and
  /// updates state.
  Future<void> _updateMarkers([double updatedZoom]) async {
    if (_clusterManager == null || updatedZoom == _currentZoom) return;

    if (updatedZoom != null) {
      _currentZoom = updatedZoom;
    }

    final updatedMarkers = await Clustering.getClusterMarkers(
      context,
      _clusterManager,
      _currentZoom,
    );

    setState(() {});
  }
}
