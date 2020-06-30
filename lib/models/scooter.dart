import 'package:nearby_mobility/models/coordinate.dart';

class Scooter {
  final String memberByString;
  final Coordinate coordinate;
  final double distance;

  Scooter(this.memberByString, this.coordinate, this.distance);

  factory Scooter.fromJson(Map<String, dynamic> json) {
    return Scooter(
      json['memberByString'],
      Coordinate.fromJson(json['coordinate']),
      json['distance'],
    );
  }
}
