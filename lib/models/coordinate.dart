class Coordinate {
  final double latitude;
  final double longitude;

  Coordinate(this.latitude, this.longitude);

  factory Coordinate.fromJson(Map<String, dynamic> json) {
    return Coordinate(json['latitude'], json['longitude']);
  }
}
