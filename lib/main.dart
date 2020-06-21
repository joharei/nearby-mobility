import 'package:flutter/material.dart';
import 'package:wear/wear.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Nearby Mobility',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: Scaffold(
        body: Center(
          child: WatchShape(builder: (context, shape) {
            return Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                Text(
                  'Shape: ${shape == Shape.round ? 'round' : 'square'}',
                ),
                AmbientMode(builder: (context, mode) {
                  return Text(
                    'Mode: ${mode == Mode.active ? 'Active' : 'Ambient'}',
                  );
                }),
              ],
            );
          }),
        ),
      ),
    );
  }
}
