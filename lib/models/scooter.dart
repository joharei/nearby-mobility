import 'package:nearby_mobility/models/coordinate.dart';

class Scooter {
  final Coordinate coordinate;
  final double distance;

  Scooter(this.coordinate, this.distance);

  factory Scooter.fromJson(Map<String, dynamic> json) {
    return Scooter(
      Coordinate.fromJson(json['coordinate']),
      json['distance'],
    );
  }
}
