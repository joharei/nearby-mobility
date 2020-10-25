import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:nearby_mobility/inherited_ambient_mode.dart';
import 'package:nearby_mobility/pages/ambient_page.dart';
import 'package:nearby_mobility/pages/map_page.dart';
import 'package:wear/wear.dart';

class MainPage extends StatefulWidget {
  MainPage({Key key}) : super(key: key);

  @override
  _MainPageState createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  @override
  Widget build(BuildContext context) {
    if (InheritedAmbientMode.of(context).mode == Mode.ambient) {
      return AmbientPage();
    }

    return FutureBuilder<Position>(
      future: Geolocator().getCurrentPosition(
          locationPermissionLevel: GeolocationPermission.locationWhenInUse),
      builder: (context, snapshot) {
        if (!snapshot.hasData) {
          return Center(child: CircularProgressIndicator());
        }

        return Stack(children: [MapPage(initialPosition: snapshot.data)]);
      },
    );
  }
}
