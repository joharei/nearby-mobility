import 'dart:developer' as developer;

import 'package:flutter/material.dart';
import 'package:nearby_mobility/icons.dart';
import 'package:nearby_mobility/models/ryde_response.dart';
import 'package:nearby_mobility/repository.dart';
import 'package:wear/wear.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  Future<RydeResponse> futureScooters;

  @override
  void initState() {
    super.initState();
    futureScooters = fetchScooters();
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
            body: FutureBuilder(
              future: futureScooters,
              builder: (context, snapshot) {
                if (snapshot.hasData) {
                  return ListView.builder(
                    itemCount: snapshot.data.scooters.length,
                    itemBuilder: (context, index) {
                      return ListTile(
                          leading: Icon(MyIcons.electric_scooter),
                          title: Text(snapshot.data.scooters[index].distance
                              .toString()));
                    },
                  );
                }
                if (snapshot.hasError) {
                  developer.log(
                    'Failed to load scooters',
                    name: 'nearby_mobility',
                    error: snapshot.error.toString(),
                  );
                  return Center(child: Text('Failed to get scooters'));
                }
                return Center(child: CircularProgressIndicator());
              },
            ),
          ),
        );
      },
    );
  }
}
