import 'package:flutter/material.dart';

/// Widget qui prévient les erreurs de RenderFlow en gérant automatiquement
/// le scroll et les contraintes sur différentes tailles d'écran
class SafeScrollView extends StatelessWidget {
  final Widget child;
  final EdgeInsetsGeometry? padding;
  final bool shrinkWrap;
  final ScrollPhysics? physics;
  final Axis scrollDirection;

  const SafeScrollView({
    super.key,
    required this.child,
    this.padding,
    this.shrinkWrap = false,
    this.physics,
    this.scrollDirection = Axis.vertical,
  });

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        return SingleChildScrollView(
          scrollDirection: scrollDirection,
          physics: physics ?? const ClampingScrollPhysics(),
          padding: padding,
          child: ConstrainedBox(
            constraints: BoxConstraints(
              minHeight: scrollDirection == Axis.vertical && !shrinkWrap
                  ? constraints.maxHeight
                  : 0,
              minWidth: scrollDirection == Axis.horizontal && !shrinkWrap
                  ? constraints.maxWidth
                  : 0,
            ),
            child: IntrinsicHeight(
              child: child,
            ),
          ),
        );
      },
    );
  }
}

/// Widget qui adapte automatiquement un ListView pour éviter les RenderFlow
class SafeListView extends StatelessWidget {
  final List<Widget> children;
  final EdgeInsetsGeometry? padding;
  final double? itemExtent;
  final bool shrinkWrap;
  final ScrollPhysics? physics;
  final Axis scrollDirection;

  const SafeListView({
    super.key,
    required this.children,
    this.padding,
    this.itemExtent,
    this.shrinkWrap = true,
    this.physics = const NeverScrollableScrollPhysics(),
    this.scrollDirection = Axis.vertical,
  });

  @override
  Widget build(BuildContext context) {
    return ListView(
      scrollDirection: scrollDirection,
      shrinkWrap: shrinkWrap,
      physics: physics,
      padding: padding,
      itemExtent: itemExtent,
      children: children,
    );
  }
}

/// Widget qui gère les contraintes flexibles pour éviter les overflow
class FlexibleContainer extends StatelessWidget {
  final Widget child;
  final double? maxWidth;
  final double? maxHeight;
  final EdgeInsetsGeometry? padding;
  final EdgeInsetsGeometry? margin;
  final Decoration? decoration;

  const FlexibleContainer({
    super.key,
    required this.child,
    this.maxWidth,
    this.maxHeight,
    this.padding,
    this.margin,
    this.decoration,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: margin,
      padding: padding,
      decoration: decoration,
      child: LayoutBuilder(
        builder: (context, constraints) {
          return ConstrainedBox(
            constraints: BoxConstraints(
              maxWidth: maxWidth ?? constraints.maxWidth,
              maxHeight: maxHeight ?? constraints.maxHeight,
            ),
            child: child,
          );
        },
      ),
    );
  }
}

/// Widget qui gère les textes longs pour éviter les overflow
class SafeText extends StatelessWidget {
  final String text;
  final TextStyle? style;
  final int? maxLines;
  final TextOverflow overflow;
  final TextAlign? textAlign;

  const SafeText(
    this.text, {
    super.key,
    this.style,
    this.maxLines,
    this.overflow = TextOverflow.ellipsis,
    this.textAlign,
  });

  @override
  Widget build(BuildContext context) {
    return Flexible(
      child: Text(
        text,
        style: style,
        maxLines: maxLines,
        overflow: overflow,
        textAlign: textAlign,
      ),
    );
  }
}

/// Widget qui adapte automatiquement les Row/Column selon l'espace disponible
class AdaptiveLayout extends StatelessWidget {
  final List<Widget> children;
  final MainAxisAlignment mainAxisAlignment;
  final CrossAxisAlignment crossAxisAlignment;
  final double breakpoint;

  const AdaptiveLayout({
    super.key,
    required this.children,
    this.mainAxisAlignment = MainAxisAlignment.start,
    this.crossAxisAlignment = CrossAxisAlignment.center,
    this.breakpoint = 600,
  });

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        if (constraints.maxWidth >= breakpoint) {
          // Mode horizontal pour les écrans larges
          return Row(
            mainAxisAlignment: mainAxisAlignment,
            crossAxisAlignment: crossAxisAlignment,
            children: children.map((child) => Expanded(child: child)).toList(),
          );
        } else {
          // Mode vertical pour les écrans étroits
          return Column(
            mainAxisAlignment: mainAxisAlignment,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: children,
          );
        }
      },
    );
  }
}