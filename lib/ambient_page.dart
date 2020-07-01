import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:nearby_mobility/icons.dart';
import 'package:nearby_mobility/models/scooter.dart';
import 'package:nearby_mobility/repository.dart' as Repository;

class AmbientPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return FutureBuilder<List<Scooter>>(
      future: Repository.nearbyScooters,
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          return Center(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text('Nearby scooters',
                    style: Theme.of(context).textTheme.subtitle1),
                SizedBox(height: 4),
                ...snapshot.data.take(3).map((scooter) {
                  return ConstrainedBox(
                    constraints: BoxConstraints(minHeight: 48),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(MyIcons.electric_scooter),
                        SizedBox(width: 8),
                        Text('${(scooter.distance * 1000).round()} m'),
                      ],
                    ),
                  );
                }).toList(),
              ],
            ),
          );
        } else {
          return Center(child: Text('Loading...'));
        }
      },
    );
  }
}
