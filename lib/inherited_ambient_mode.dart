import 'package:flutter/widgets.dart';
import 'package:wear/wear.dart';

class InheritedAmbientMode extends InheritedWidget {
  const InheritedAmbientMode(
      {Key key, @required this.mode, @required Widget child})
      : assert(mode != null),
        assert(child != null),
        super(key: key, child: child);

  final Mode mode;

  static InheritedAmbientMode of(BuildContext context) {
    return context.dependOnInheritedWidgetOfExactType<InheritedAmbientMode>();
  }

  @override
  bool updateShouldNotify(InheritedAmbientMode old) => mode != old.mode;
}
