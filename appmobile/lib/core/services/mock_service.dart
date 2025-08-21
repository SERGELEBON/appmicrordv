import 'dart:async';
import 'dart:math';
import '../models/appointment.dart';
import '../models/chat.dart';
import '../models/user.dart';
import '../constants/app_constants.dart';

class MockService {
  static final Random _random = Random();
  
  // Données mock pour les tests
  static final List<User> _mockDoctors = [
    User(
      id: 1,
      firstName: 'Marie',
      lastName: 'Dubois',
      email: 'marie.dubois@clinic.fr',
      role: AppConstants.doctorRole,
      specialty: 'GENERALISTE',
      phone: '+33123456789',
      createdAt: DateTime.now().subtract(Duration(days: 365)),
    ),
    User(
      id: 2,
      firstName: 'Pierre',
      lastName: 'Martin',
      email: 'pierre.martin@cardio.fr',
      role: AppConstants.doctorRole,
      specialty: 'CARDIOLOGUE',
      phone: '+33123456790',
      createdAt: DateTime.now().subtract(Duration(days: 200)),
    ),
    User(
      id: 3,
      firstName: 'Sophie',
      lastName: 'Bernard',
      email: 'sophie.bernard@dermato.fr',
      role: AppConstants.doctorRole,
      specialty: 'DERMATOLOGUE',
      phone: '+33123456791',
      createdAt: DateTime.now().subtract(Duration(days: 150)),
    ),
  ];

  static final User _mockPatient = User(
    id: 100,
    firstName: 'Jean',
    lastName: 'Dupont',
    email: 'jean.dupont@email.fr',
    role: AppConstants.patientRole,
    phone: '+33987654321',
    createdAt: DateTime.now().subtract(Duration(days: 30)),
  );

  static final List<Appointment> _mockAppointments = [
    Appointment(
      id: 1,
      dateTime: DateTime.now().add(Duration(days: 2, hours: 9)),
      status: 'CONFIRMED',
      doctor: _mockDoctors[0],
      patient: _mockPatient,
      notes: 'Consultation de suivi',
      createdAt: DateTime.now().subtract(Duration(days: 1)),
      updatedAt: DateTime.now().subtract(Duration(hours: 2)),
    ),
    Appointment(
      id: 2,
      dateTime: DateTime.now().add(Duration(days: 7, hours: 14)),
      status: 'PENDING',
      doctor: _mockDoctors[1],
      patient: _mockPatient,
      notes: 'Contrôle cardiaque',
      createdAt: DateTime.now().subtract(Duration(hours: 3)),
    ),
  ];

  // Simulation d'authentification
  static Future<Map<String, dynamic>> mockLogin(String email, String password) async {
    await Future.delayed(Duration(milliseconds: 500)); // Simule latence réseau
    
    if (email == 'patient@test.fr' && password == 'test123') {
      return {
        'accessToken': 'mock_token_${DateTime.now().millisecondsSinceEpoch}',
        'refreshToken': 'mock_refresh_token_${DateTime.now().millisecondsSinceEpoch}',
        'user': _mockPatient.toJson(),
      };
    } else if (email == 'docteur@test.fr' && password == 'test123') {
      return {
        'accessToken': 'mock_token_${DateTime.now().millisecondsSinceEpoch}',
        'refreshToken': 'mock_refresh_token_${DateTime.now().millisecondsSinceEpoch}',
        'user': _mockDoctors[0].toJson(),
      };
    }
    
    throw Exception('Identifiants invalides');
  }

  // Mock des rendez-vous
  static Future<List<Appointment>> mockGetAppointments() async {
    await Future.delayed(Duration(milliseconds: 300));
    return _mockAppointments;
  }

  // Mock des docteurs
  static Future<List<Doctor>> mockGetDoctors() async {
    await Future.delayed(Duration(milliseconds: 200));
    return _mockDoctors.map((user) => Doctor(
      id: user.id,
      firstName: user.firstName,
      lastName: user.lastName,
      specialty: user.specialty ?? 'GENERALISTE',
      email: user.email,
      phone: user.phone,
      rating: 4.0 + _random.nextDouble(),
      availableSlots: _generateMockSlots(),
    )).toList();
  }

  // Mock slots disponibles
  static List<AppointmentSlot> _generateMockSlots() {
    final slots = <AppointmentSlot>[];
    final now = DateTime.now();
    
    for (int day = 1; day <= 14; day++) {
      final date = now.add(Duration(days: day));
      if (date.weekday <= 5) { // Lundi à vendredi
        for (int hour = 9; hour <= 17; hour++) {
          if (hour != 12 && hour != 13) { // Pas pendant la pause déjeuner
            slots.add(AppointmentSlot(
              dateTime: DateTime(date.year, date.month, date.day, hour),
              isAvailable: _random.nextBool(),
            ));
          }
        }
      }
    }
    
    return slots;
  }

  // Mock chat IA
  static Future<Map<String, dynamic>> mockChatMessage(String message) async {
    await Future.delayed(Duration(milliseconds: 800)); // Simule réflexion IA
    
    final responses = [
      'Basé sur vos symptômes, je recommande de consulter un médecin généraliste.',
      'Ces symptômes pourraient nécessiter une consultation avec un spécialiste.',
      'Je vous suggère de prendre rendez-vous dans les prochains jours.',
      'Ces symptômes sont généralement bénins, mais une consultation peut rassurer.',
    ];
    
    final specialties = ['GENERALISTE', 'CARDIOLOGUE', 'DERMATOLOGUE'];
    
    return {
      'message': responses[_random.nextInt(responses.length)],
      'conversationId': 'mock_conversation_${DateTime.now().millisecondsSinceEpoch}',
      'suggestedSpecialty': _random.nextBool() ? specialties[_random.nextInt(specialties.length)] : null,
      'aiMetadata': {
        'confidence': 0.7 + _random.nextDouble() * 0.3,
        'processingTime': '${500 + _random.nextInt(500)}ms',
      },
    };
  }

  // Mode développement
  static bool get isDevelopmentMode => true; // À changer en production
}