import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:nearby_mobility/env.dart';
import 'package:nearby_mobility/models/ryde_response.dart';

Future<RydeResponse> fetchScooters() async {
  final response = await http.post(
    '$rydeBaseUrl/appRyde/getNearScooters',
    body: {
      'iotLa': '58.95916749985502',
      'iotLo': '5.739936344325542',
      'nearRadius': '1',
      'cityId': '7',
    },
    headers: {"Content-Type": "application/x-www-form-urlencoded"},
  );

  if (response.statusCode == 200) {
    return RydeResponse.fromJson(json.decode(response.body));
  } else {
    throw Exception('Failed to load scooters');
  }
}
