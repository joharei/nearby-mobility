import 'package:flutter/material.dart';
import 'package:nearby_mobility/ambient_page.dart';
import 'package:nearby_mobility/map_page.dart';
import 'package:wear/wear.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) {
    return AmbientMode(
      builder: (context, mode) {
        return MaterialApp(
          title: 'Nearby Mobility',
          darkTheme: ThemeData(
            brightness: Brightness.dark,
            canvasColor: mode == Mode.ambient ? Colors.black : null,
            visualDensity: VisualDensity.adaptivePlatformDensity,
          ),
          home: Scaffold(
            body: mode == Mode.ambient
                ? AmbientPage()
                : Stack(
                    children: [MapPage()],
                  ),
          ),
        );
      },
    );
  }
}
