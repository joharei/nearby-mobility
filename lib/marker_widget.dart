import 'package:flutter/material.dart';
import 'package:nearby_mobility/icons.dart';

class MarkerWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.all(4),
      child: Material(
        color: Color(0xFFDCF2D7),
        shape: CircleBorder(),
        elevation: 2,
        child: Padding(
          padding: const EdgeInsets.all(4),
          child: Icon(MyIcons.electric_scooter, size: 14),
        ),
      ),
    );
  }
}
