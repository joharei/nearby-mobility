import 'dart:convert';
import 'dart:developer' as developer;

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
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
  );

  if (response.statusCode == 200) {
    developer.log('Got response: ${response.body}');
    return RydeResponse.fromJson(json.decode(response.body));
  } else {
    developer.log('Failed to load scooters', error: response.reasonPhrase);
    throw Exception('Failed to load scooters');
  }
}
