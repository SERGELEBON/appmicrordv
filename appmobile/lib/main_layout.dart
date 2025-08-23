import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'core/constants/app_constants.dart';
import 'core/providers/auth_provider.dart';
import 'core/theme/app_theme.dart';
import 'core/utils/responsive_utils.dart';
import 'shared/widgets/top_navigation_bar.dart';

class MainLayout extends ConsumerStatefulWidget {
  final Widget child;

  const MainLayout({super.key, required this.child});

  @override
  ConsumerState<MainLayout> createState() => _MainLayoutState();
}

class _MainLayoutState extends ConsumerState<MainLayout> {
  int _selectedIndex = 0;

  final List<NavigationItem> _patientNavItems = [
    NavigationItem(
      label: 'Accueil',
      icon: Icons.home,
      route: AppConstants.homeRoute,
    ),
    NavigationItem(
      label: 'Rendez-vous',
      icon: Icons.calendar_today,
      route: AppConstants.appointmentsRoute,
    ),
    NavigationItem(
      label: 'Assistant IA',
      icon: Icons.smart_toy,
      route: AppConstants.chatRoute,
    ),
    NavigationItem(
      label: 'Symptômes',
      icon: Icons.health_and_safety,
      route: '/symptom-checker',
    ),
    NavigationItem(
      label: 'Profil',
      icon: Icons.person,
      route: AppConstants.profileRoute,
    ),
  ];

  final List<NavigationItem> _doctorNavItems = [
    NavigationItem(
      label: 'Accueil',
      icon: Icons.home,
      route: AppConstants.homeRoute,
    ),
    NavigationItem(
      label: 'Patients',
      icon: Icons.people,
      route: AppConstants.appointmentsRoute,
    ),
    NavigationItem(
      label: 'IA Médical',
      icon: Icons.psychology,
      route: AppConstants.chatRoute,
    ),
    NavigationItem(
      label: 'Profil',
      icon: Icons.person,
      route: AppConstants.profileRoute,
    ),
  ];

  @override
  Widget build(BuildContext context) {
    // Optimisation : Surveillance sélective pour réduire les rebuilds
    final user = ref.watch(authStateProvider.select((state) => state.user));

    if (user == null) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    final isPatient = user.role == AppConstants.patientRole;
    final navItems = isPatient ? _patientNavItems : _doctorNavItems;
    
    // Déterminer l'index sélectionné basé sur la route actuelle
    final currentLocation = GoRouterState.of(context).uri.toString();
    _selectedIndex = _getSelectedIndex(currentLocation, navItems);

    // Utiliser NavigationRail pour tablettes et desktop
    if (ResponsiveUtils.isTablet(context) || ResponsiveUtils.isDesktop(context)) {
      return Scaffold(
        body: Row(
          children: [
            NavigationRail(
              selectedIndex: _selectedIndex,
              onDestinationSelected: (index) {
                context.go(navItems[index].route);
              },
              backgroundColor: Theme.of(context).cardColor,
              selectedIconTheme: IconThemeData(color: AppTheme.primaryColor),
              unselectedIconTheme: IconThemeData(color: AppTheme.textSecondary),
              selectedLabelTextStyle: TextStyle(color: AppTheme.primaryColor),
              unselectedLabelTextStyle: TextStyle(color: AppTheme.textSecondary),
              labelType: ResponsiveUtils.isDesktop(context) 
                  ? NavigationRailLabelType.all 
                  : NavigationRailLabelType.selected,
              destinations: navItems.map((item) => NavigationRailDestination(
                icon: Icon(item.icon),
                label: Text(item.label),
              )).toList(),
            ),
            VerticalDivider(thickness: 1, width: 1),
            Expanded(child: widget.child),
          ],
        ),
      );
    }

    // Utiliser BottomNavigationBar pour mobile
    return Scaffold(
      appBar: const TopNavigationBar(),
      body: widget.child,
      bottomNavigationBar: BottomNavigationBar(
        type: BottomNavigationBarType.fixed,
        currentIndex: _selectedIndex,
        onTap: (index) {
          context.go(navItems[index].route);
        },
        selectedItemColor: Colors.white,
        unselectedItemColor: Colors.white.withOpacity(0.7),
        elevation: 8,
        backgroundColor: AppTheme.secondaryColor, // Bleu pur
        items: navItems.map((item) => BottomNavigationBarItem(
          icon: Icon(item.icon),
          label: item.label,
        )).toList(),
      ),
    );
  }

  int _getSelectedIndex(String currentLocation, List<NavigationItem> navItems) {
    for (int i = 0; i < navItems.length; i++) {
      if (currentLocation.startsWith(navItems[i].route)) {
        return i;
      }
    }
    return 0; // Default to home if no match
  }
}

class NavigationItem {
  final String label;
  final IconData icon;
  final String route;

  NavigationItem({
    required this.label,
    required this.icon,
    required this.route,
  });
}