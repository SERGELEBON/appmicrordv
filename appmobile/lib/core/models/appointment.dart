import 'package:freezed_annotation/freezed_annotation.dart';
import 'user.dart';

part 'appointment.freezed.dart';
part 'appointment.g.dart';

@freezed
class Appointment with _$Appointment {
  const factory Appointment({
    required int id,
    required DateTime dateTime,
    required String status,
    required User doctor,
    required User patient,
    String? notes,
    String? diagnosis,
    String? prescription,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) = _Appointment;

  factory Appointment.fromJson(Map<String, dynamic> json) => 
      _$AppointmentFromJson(json);
}

@freezed
class AppointmentRequest with _$AppointmentRequest {
  const factory AppointmentRequest({
    required int doctorId,
    required DateTime dateTime,
    String? notes,
  }) = _AppointmentRequest;

  factory AppointmentRequest.fromJson(Map<String, dynamic> json) => 
      _$AppointmentRequestFromJson(json);
}

@freezed
class AppointmentSlot with _$AppointmentSlot {
  const factory AppointmentSlot({
    required DateTime dateTime,
    required bool isAvailable,
    int? appointmentId,
  }) = _AppointmentSlot;

  factory AppointmentSlot.fromJson(Map<String, dynamic> json) => 
      _$AppointmentSlotFromJson(json);
}

@freezed
class Doctor with _$Doctor {
  const factory Doctor({
    required int id,
    required String firstName,
    required String lastName,
    required String specialty,
    String? phone,
    String? email,
    String? address,
    double? rating,
    List<AppointmentSlot>? availableSlots,
  }) = _Doctor;

  factory Doctor.fromJson(Map<String, dynamic> json) => 
      _$DoctorFromJson(json);
}