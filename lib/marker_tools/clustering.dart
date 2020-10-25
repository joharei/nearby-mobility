import 'package:fluster/fluster.dart';
import 'package:flutter/cupertino.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:nearby_mobility/marker_tools/map_marker.dart';
import 'package:nearby_mobility/marker_tools/marker_generator.dart';
import 'package:nearby_mobility/marker_tools/marker_widget.dart';

/// Inits the cluster manager with all the [MapMarker] to be displayed on the map.
/// Here we're also setting up the cluster marker itself, also with an [clusterImageUrl].
///
/// For more info about customizing your clustering logic check the [Fluster] constructor.
Future<Fluster<MapMarker>> initClusterManager(
  List<MapMarker> markers,
  int minZoom,
  int maxZoom,
) async {
  assert(markers != null);
  assert(minZoom != null);
  assert(maxZoom != null);

  return Fluster<MapMarker>(
    minZoom: minZoom,
    maxZoom: maxZoom,
    radius: 150,
    extent: 2048,
    nodeSize: 64,
    points: markers,
    createCluster: (
      BaseCluster cluster,
      double lng,
      double lat,
    ) =>
        MapMarker(
      id: cluster.id.toString(),
      position: LatLng(lat, lng),
      isCluster: cluster.isCluster,
      clusterId: cluster.id,
      pointsSize: cluster.pointsSize,
      childMarkerId: cluster.childMarkerId,
    ),
  );
}

/// Gets a list of markers and clusters that reside within the visible bounding box for
/// the given [currentZoom]. For more info check [Fluster.clusters].
Future<List<Marker>> getClusterMarkers(
  BuildContext context,
  Fluster<MapMarker> clusterManager,
  double currentZoom,
) {
  assert(currentZoom != null);

  if (clusterManager == null) return Future.value([]);

  return Future.wait(clusterManager.clusters(
      [-180, -85, 180, 85], currentZoom.toInt()).map((mapMarker) async {
    if (mapMarker.isCluster) {
      mapMarker.icon = await generateIcon(context, MarkerWidget());
    }

    return mapMarker.toMarker();
  }).toList());
}
