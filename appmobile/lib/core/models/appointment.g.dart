// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'appointment.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$AppointmentImpl _$$AppointmentImplFromJson(Map<String, dynamic> json) =>
    _$AppointmentImpl(
      id: (json['id'] as num).toInt(),
      dateTime: DateTime.parse(json['dateTime'] as String),
      status: json['status'] as String,
      doctor: User.fromJson(json['doctor'] as Map<String, dynamic>),
      patient: User.fromJson(json['patient'] as Map<String, dynamic>),
      notes: json['notes'] as String?,
      diagnosis: json['diagnosis'] as String?,
      prescription: json['prescription'] as String?,
      createdAt: json['createdAt'] == null
          ? null
          : DateTime.parse(json['createdAt'] as String),
      updatedAt: json['updatedAt'] == null
          ? null
          : DateTime.parse(json['updatedAt'] as String),
    );

Map<String, dynamic> _$$AppointmentImplToJson(_$AppointmentImpl instance) =>
    <String, dynamic>{
      'id': instance.id,
      'dateTime': instance.dateTime.toIso8601String(),
      'status': instance.status,
      'doctor': instance.doctor.toJson(),
      'patient': instance.patient.toJson(),
      'notes': instance.notes,
      'diagnosis': instance.diagnosis,
      'prescription': instance.prescription,
      'createdAt': instance.createdAt?.toIso8601String(),
      'updatedAt': instance.updatedAt?.toIso8601String(),
    };

_$AppointmentRequestImpl _$$AppointmentRequestImplFromJson(
        Map<String, dynamic> json) =>
    _$AppointmentRequestImpl(
      doctorId: (json['doctorId'] as num).toInt(),
      dateTime: DateTime.parse(json['dateTime'] as String),
      notes: json['notes'] as String?,
    );

Map<String, dynamic> _$$AppointmentRequestImplToJson(
        _$AppointmentRequestImpl instance) =>
    <String, dynamic>{
      'doctorId': instance.doctorId,
      'dateTime': instance.dateTime.toIso8601String(),
      'notes': instance.notes,
    };

_$AppointmentSlotImpl _$$AppointmentSlotImplFromJson(
        Map<String, dynamic> json) =>
    _$AppointmentSlotImpl(
      dateTime: DateTime.parse(json['dateTime'] as String),
      isAvailable: json['isAvailable'] as bool,
      appointmentId: (json['appointmentId'] as num?)?.toInt(),
    );

Map<String, dynamic> _$$AppointmentSlotImplToJson(
        _$AppointmentSlotImpl instance) =>
    <String, dynamic>{
      'dateTime': instance.dateTime.toIso8601String(),
      'isAvailable': instance.isAvailable,
      'appointmentId': instance.appointmentId,
    };

_$DoctorImpl _$$DoctorImplFromJson(Map<String, dynamic> json) => _$DoctorImpl(
      id: (json['id'] as num).toInt(),
      firstName: json['firstName'] as String,
      lastName: json['lastName'] as String,
      specialty: json['specialty'] as String,
      phone: json['phone'] as String?,
      email: json['email'] as String?,
      address: json['address'] as String?,
      rating: (json['rating'] as num?)?.toDouble(),
      availableSlots: (json['availableSlots'] as List<dynamic>?)
          ?.map((e) => AppointmentSlot.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$$DoctorImplToJson(_$DoctorImpl instance) =>
    <String, dynamic>{
      'id': instance.id,
      'firstName': instance.firstName,
      'lastName': instance.lastName,
      'specialty': instance.specialty,
      'phone': instance.phone,
      'email': instance.email,
      'address': instance.address,
      'rating': instance.rating,
      'availableSlots': instance.availableSlots?.map((e) => e.toJson()).toList(),
    };
