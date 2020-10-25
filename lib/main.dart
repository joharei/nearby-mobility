import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:nearby_mobility/inherited_ambient_mode.dart';
import 'package:nearby_mobility/pages/main_page.dart';
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
        return InheritedAmbientMode(
          mode: mode,
          child: MaterialApp(
            title: 'Nearby Mobility',
            theme: ThemeData(
              primarySwatch: Colors.blue,
              visualDensity: VisualDensity.adaptivePlatformDensity,
            ),
            darkTheme: ThemeData(
              brightness: Brightness.dark,
              scaffoldBackgroundColor:
                  mode == Mode.ambient ? Colors.black : null,
              canvasColor: mode == Mode.ambient ? Colors.black : null,
              backgroundColor: Colors.black,
              visualDensity: VisualDensity.adaptivePlatformDensity,
            ),
            themeMode: ThemeMode.dark,
            home: Scaffold(body: MainPage()),
          ),
        );
      },
    );
  }
}
