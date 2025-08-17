class AppConstants {
  static const String appName = 'RDV Chez Doc';
  
  // API Configuration
  static const String baseUrl = 'http://10.0.2.2:8080/api';
  static const String authServiceUrl = 'http://10.0.2.2:8080/api/auth';
  static const String rdvServiceUrl = 'http://10.0.2.2:8080/api/rdv';
  static const String chatServiceUrl = 'http://10.0.2.2:8080/api/chat';
  
  // Storage Keys
  static const String tokenKey = 'auth_token';
  static const String refreshTokenKey = 'refresh_token';
  static const String userDataKey = 'user_data';
  
  // Routes
  static const String loginRoute = '/login';
  static const String registerRoute = '/register';
  static const String forgotPasswordRoute = '/forgot-password';
  static const String homeRoute = '/home';
  static const String profileRoute = '/profile';
  static const String appointmentsRoute = '/appointments';
  static const String chatRoute = '/chat';
  static const String doctorsRoute = '/doctors';
  
  // API Endpoints
  static const String loginEndpoint = '/login';
  static const String registerEndpoint = '/register';
  static const String refreshTokenEndpoint = '/refresh';
  static const String appointmentsEndpoint = '/appointments';
  static const String doctorsEndpoint = '/doctors';
  static const String patientsEndpoint = '/patients';
  static const String chatEndpoint = '/chat';
  
  // User Roles
  static const String patientRole = 'PATIENT';
  static const String doctorRole = 'MEDECIN';


  // Notification Types
  static const String appointmentReminder = 'appointment_reminder';
  static const String aiSuggestion = 'ai_suggestion';
  
  // Medical Specialties
  static const List<String> medicalSpecialties = [
    'GENERALISTE',
    'CARDIOLOGUE',
    'DERMATOLOGUE',
    'GYNECOLOGUE',
    'NEUROLOGUE',
    'OPHTALMOLOGUE',
    'ORL',
    'PEDIATRE',
    'PSYCHIATRE',
    'RADIOLOGUE'
  ];
}