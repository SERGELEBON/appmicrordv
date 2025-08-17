import 'package:flutter/material.dart';

class ResponsiveUtils {
  static const double mobileBreakpoint = 600;
  static const double tabletBreakpoint = 900;
  static const double desktopBreakpoint = 1200;

  static bool isMobile(BuildContext context) {
    return MediaQuery.of(context).size.width < mobileBreakpoint;
  }

  static bool isTablet(BuildContext context) {
    final width = MediaQuery.of(context).size.width;
    return width >= mobileBreakpoint && width < tabletBreakpoint;
  }

  static bool isDesktop(BuildContext context) {
    return MediaQuery.of(context).size.width >= tabletBreakpoint;
  }

  static bool isLandscape(BuildContext context) {
    return MediaQuery.of(context).orientation == Orientation.landscape;
  }

  static double getScreenWidth(BuildContext context) {
    return MediaQuery.of(context).size.width;
  }

  static double getScreenHeight(BuildContext context) {
    return MediaQuery.of(context).size.height;
  }

  static EdgeInsets getResponsivePadding(BuildContext context) {
    if (isDesktop(context)) {
      return const EdgeInsets.all(24);
    } else if (isTablet(context)) {
      return const EdgeInsets.all(20);
    } else {
      return const EdgeInsets.all(16);
    }
  }

  static int getGridColumns(BuildContext context) {
    if (isDesktop(context)) {
      return 4;
    } else if (isTablet(context)) {
      return isLandscape(context) ? 4 : 3;
    } else {
      return isLandscape(context) ? 3 : 2;
    }
  }

  static double getCardWidth(BuildContext context) {
    final screenWidth = getScreenWidth(context);
    if (isDesktop(context)) {
      return (screenWidth - 120) / 4; // 4 colonnes avec espacement
    } else if (isTablet(context)) {
      return isLandscape(context) 
          ? (screenWidth - 100) / 4 
          : (screenWidth - 80) / 3;
    } else {
      return isLandscape(context) 
          ? (screenWidth - 60) / 3 
          : (screenWidth - 40) / 2;
    }
  }

  static double getFontSize(BuildContext context, double baseFontSize) {
    if (isDesktop(context)) {
      return baseFontSize * 1.1;
    } else if (isTablet(context)) {
      return baseFontSize * 1.05;
    } else {
      return baseFontSize;
    }
  }

  static double getIconSize(BuildContext context, double baseIconSize) {
    if (isDesktop(context)) {
      return baseIconSize * 1.2;
    } else if (isTablet(context)) {
      return baseIconSize * 1.1;
    } else {
      return baseIconSize;
    }
  }

  static double getAppBarHeight(BuildContext context) {
    if (isDesktop(context)) {
      return 64;
    } else if (isTablet(context)) {
      return 60;
    } else {
      return 56;
    }
  }

  static EdgeInsets getSafeAreaPadding(BuildContext context) {
    final padding = MediaQuery.of(context).padding;
    return EdgeInsets.only(
      top: padding.top,
      bottom: padding.bottom,
      left: padding.left,
      right: padding.right,
    );
  }

  // Helper pour les grilles responsive
  static Widget buildResponsiveGrid({
    required BuildContext context,
    required List<Widget> children,
    double? childAspectRatio,
    double mainAxisSpacing = 12,
    double crossAxisSpacing = 12,
  }) {
    final columns = getGridColumns(context);
    final aspectRatio = childAspectRatio ?? (isTablet(context) || isDesktop(context) ? 1.3 : 1.2);

    return GridView.builder(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: columns,
        childAspectRatio: aspectRatio,
        mainAxisSpacing: mainAxisSpacing,
        crossAxisSpacing: crossAxisSpacing,
      ),
      itemCount: children.length,
      itemBuilder: (context, index) => children[index],
    );
  }

  // Helper pour les layouts adaptifs
  static Widget buildAdaptiveLayout({
    required BuildContext context,
    required Widget mobileLayout,
    Widget? tabletLayout,
    Widget? desktopLayout,
  }) {
    if (isDesktop(context) && desktopLayout != null) {
      return desktopLayout;
    } else if (isTablet(context) && tabletLayout != null) {
      return tabletLayout;
    } else {
      return mobileLayout;
    }
  }

  // Calculer la largeur maximale du contenu pour éviter l'étirement sur grand écran
  static double getMaxContentWidth(BuildContext context) {
    final screenWidth = getScreenWidth(context);
    if (isDesktop(context)) {
      return 1200; // Largeur max sur desktop
    } else if (isTablet(context)) {
      return 800; // Largeur max sur tablette
    } else {
      return screenWidth; // Pleine largeur sur mobile
    }
  }

  // Wrapper pour centrer le contenu sur les grands écrans
  static Widget buildCenteredContent({
    required BuildContext context,
    required Widget child,
  }) {
    final maxWidth = getMaxContentWidth(context);
    
    return Center(
      child: ConstrainedBox(
        constraints: BoxConstraints(maxWidth: maxWidth),
        child: child,
      ),
    );
  }
}