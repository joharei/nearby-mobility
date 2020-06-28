import 'package:nearby_mobility/models/scooter.dart';

class RydeResponse {
  final String message;
  final List<Scooter> scooters;

  RydeResponse(this.message, this.scooters);

  factory RydeResponse.fromJson(Map<String, dynamic> json) {
    return RydeResponse(
      json['message'],
      json['scooters']
          .map((scooterJson) => Scooter.fromJson(scooterJson))
          .cast<Scooter>()
          .toList(),
    );
  }
}
