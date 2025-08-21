import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/appointment.dart';
import '../services/appointment_service.dart';
import '../services/mock_service.dart';
import '../constants/app_constants.dart';
import 'auth_provider.dart';

final appointmentServiceProvider = Provider<AppointmentService>((ref) {
  final apiService = ref.read(apiServiceProvider);
  return AppointmentService(apiService.dio);
});

final appointmentsProvider = StateNotifierProvider<AppointmentsNotifier, AppointmentsState>((ref) {
  final appointmentService = ref.read(appointmentServiceProvider);
  return AppointmentsNotifier(appointmentService);
});

final doctorsProvider = StateNotifierProvider<DoctorsNotifier, DoctorsState>((ref) {
  final appointmentService = ref.read(appointmentServiceProvider);
  return DoctorsNotifier(appointmentService);
});

class AppointmentsState {
  final List<Appointment> appointments;
  final bool isLoading;
  final String? error;

  const AppointmentsState({
    this.appointments = const [],
    this.isLoading = false,
    this.error,
  });

  AppointmentsState copyWith({
    List<Appointment>? appointments,
    bool? isLoading,
    String? error,
  }) {
    return AppointmentsState(
      appointments: appointments ?? this.appointments,
      isLoading: isLoading ?? this.isLoading,
      error: error,
    );
  }
}

class DoctorsState {
  final List<Doctor> doctors;
  final Doctor? selectedDoctor;
  final List<AppointmentSlot> availableSlots;
  final bool isLoading;
  final String? error;

  const DoctorsState({
    this.doctors = const [],
    this.selectedDoctor,
    this.availableSlots = const [],
    this.isLoading = false,
    this.error,
  });

  DoctorsState copyWith({
    List<Doctor>? doctors,
    Doctor? selectedDoctor,
    List<AppointmentSlot>? availableSlots,
    bool? isLoading,
    String? error,
  }) {
    return DoctorsState(
      doctors: doctors ?? this.doctors,
      selectedDoctor: selectedDoctor ?? this.selectedDoctor,
      availableSlots: availableSlots ?? this.availableSlots,
      isLoading: isLoading ?? this.isLoading,
      error: error,
    );
  }
}

class AppointmentsNotifier extends StateNotifier<AppointmentsState> {
  final AppointmentService _appointmentService;

  AppointmentsNotifier(this._appointmentService) : super(const AppointmentsState());

  Future<void> loadAppointments({bool forceRefresh = false}) async {
    state = state.copyWith(isLoading: true, error: null);
    
    try {
      List<Appointment> appointments;
      
      if (AppConstants.useMockData) {
        appointments = await MockService.mockGetAppointments();
      } else {
        // Note: forceRefresh ignoré car l'API ne le supporte pas encore
        appointments = await _appointmentService.getAppointments();
      }
      
      state = state.copyWith(appointments: appointments, isLoading: false);
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: 'Erreur lors du chargement des rendez-vous',
      );
    }
  }

  Future<bool> createAppointment(AppointmentRequest request) async {
    try {
      final appointment = await _appointmentService.createAppointment(request);
      state = state.copyWith(
        appointments: [...state.appointments, appointment],
      );
      return true;
    } catch (e) {
      state = state.copyWith(error: 'Erreur lors de la création du rendez-vous');
      return false;
    }
  }

  Future<bool> cancelAppointment(int appointmentId) async {
    try {
      await _appointmentService.cancelAppointment(appointmentId);
      state = state.copyWith(
        appointments: state.appointments
            .where((apt) => apt.id != appointmentId)
            .toList(),
      );
      return true;
    } catch (e) {
      state = state.copyWith(error: 'Erreur lors de l\'annulation du rendez-vous');
      return false;
    }
  }

  Future<bool> rescheduleAppointment(int appointmentId, DateTime newDateTime) async {
    try {
      final updatedAppointment = await _appointmentService.rescheduleAppointment(
        appointmentId,
        {'newDateTime': newDateTime.toIso8601String()},
      );
      
      final updatedAppointments = state.appointments
          .map((apt) => apt.id == appointmentId ? updatedAppointment : apt)
          .toList();
      
      state = state.copyWith(appointments: updatedAppointments);
      return true;
    } catch (e) {
      state = state.copyWith(error: 'Erreur lors de la reprogrammation du rendez-vous');
      return false;
    }
  }

  void clearError() {
    state = state.copyWith(error: null);
  }
}

class DoctorsNotifier extends StateNotifier<DoctorsState> {
  final AppointmentService _appointmentService;

  DoctorsNotifier(this._appointmentService) : super(const DoctorsState());

  Future<void> loadDoctors() async {
    state = state.copyWith(isLoading: true, error: null);
    
    try {
      List<Doctor> doctors;
      
      if (AppConstants.useMockData) {
        doctors = await MockService.mockGetDoctors();
      } else {
        doctors = await _appointmentService.getDoctors();
      }
      
      state = state.copyWith(doctors: doctors, isLoading: false);
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: 'Erreur lors du chargement des médecins',
      );
    }
  }

  Future<void> loadDoctorsBySpecialty(String specialty) async {
    state = state.copyWith(isLoading: true, error: null);
    
    try {
      final doctors = await _appointmentService.getDoctorsBySpecialty(specialty);
      state = state.copyWith(doctors: doctors, isLoading: false);
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: 'Erreur lors du chargement des médecins par spécialité',
      );
    }
  }

  Future<void> loadDoctorAvailability(int doctorId, DateTime date) async {
    state = state.copyWith(isLoading: true, error: null);
    
    try {
      final slots = await _appointmentService.getDoctorAvailability(
        doctorId,
        date.toIso8601String().split('T')[0],
      );
      state = state.copyWith(availableSlots: slots, isLoading: false);
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: 'Erreur lors du chargement des créneaux disponibles',
      );
    }
  }

  void selectDoctor(Doctor doctor) {
    state = state.copyWith(selectedDoctor: doctor);
  }

  void clearError() {
    state = state.copyWith(error: null);
  }
}