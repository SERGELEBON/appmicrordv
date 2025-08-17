// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'appointment.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models');

Appointment _$AppointmentFromJson(Map<String, dynamic> json) {
  return _Appointment.fromJson(json);
}

/// @nodoc
mixin _$Appointment {
  int get id => throw _privateConstructorUsedError;
  DateTime get dateTime => throw _privateConstructorUsedError;
  String get status => throw _privateConstructorUsedError;
  User get doctor => throw _privateConstructorUsedError;
  User get patient => throw _privateConstructorUsedError;
  String? get notes => throw _privateConstructorUsedError;
  String? get diagnosis => throw _privateConstructorUsedError;
  String? get prescription => throw _privateConstructorUsedError;
  DateTime? get createdAt => throw _privateConstructorUsedError;
  DateTime? get updatedAt => throw _privateConstructorUsedError;

  /// Serializes this Appointment to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of Appointment
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $AppointmentCopyWith<Appointment> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $AppointmentCopyWith<$Res> {
  factory $AppointmentCopyWith(
          Appointment value, $Res Function(Appointment) then) =
      _$AppointmentCopyWithImpl<$Res, Appointment>;
  @useResult
  $Res call(
      {int id,
      DateTime dateTime,
      String status,
      User doctor,
      User patient,
      String? notes,
      String? diagnosis,
      String? prescription,
      DateTime? createdAt,
      DateTime? updatedAt});

  $UserCopyWith<$Res> get doctor;
  $UserCopyWith<$Res> get patient;
}

/// @nodoc
class _$AppointmentCopyWithImpl<$Res, $Val extends Appointment>
    implements $AppointmentCopyWith<$Res> {
  _$AppointmentCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of Appointment
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? dateTime = null,
    Object? status = null,
    Object? doctor = null,
    Object? patient = null,
    Object? notes = freezed,
    Object? diagnosis = freezed,
    Object? prescription = freezed,
    Object? createdAt = freezed,
    Object? updatedAt = freezed,
  }) {
    return _then(_value.copyWith(
      id: null == id
          ? _value.id
          : id // ignore: cast_nullable_to_non_nullable
              as int,
      dateTime: null == dateTime
          ? _value.dateTime
          : dateTime // ignore: cast_nullable_to_non_nullable
              as DateTime,
      status: null == status
          ? _value.status
          : status // ignore: cast_nullable_to_non_nullable
              as String,
      doctor: null == doctor
          ? _value.doctor
          : doctor // ignore: cast_nullable_to_non_nullable
              as User,
      patient: null == patient
          ? _value.patient
          : patient // ignore: cast_nullable_to_non_nullable
              as User,
      notes: freezed == notes
          ? _value.notes
          : notes // ignore: cast_nullable_to_non_nullable
              as String?,
      diagnosis: freezed == diagnosis
          ? _value.diagnosis
          : diagnosis // ignore: cast_nullable_to_non_nullable
              as String?,
      prescription: freezed == prescription
          ? _value.prescription
          : prescription // ignore: cast_nullable_to_non_nullable
              as String?,
      createdAt: freezed == createdAt
          ? _value.createdAt
          : createdAt // ignore: cast_nullable_to_non_nullable
              as DateTime?,
      updatedAt: freezed == updatedAt
          ? _value.updatedAt
          : updatedAt // ignore: cast_nullable_to_non_nullable
              as DateTime?,
    ) as $Val);
  }

  /// Create a copy of Appointment
  /// with the given fields replaced by the non-null parameter values.
  @override
  @pragma('vm:prefer-inline')
  $UserCopyWith<$Res> get doctor {
    return $UserCopyWith<$Res>(_value.doctor, (value) {
      return _then(_value.copyWith(doctor: value) as $Val);
    });
  }

  /// Create a copy of Appointment
  /// with the given fields replaced by the non-null parameter values.
  @override
  @pragma('vm:prefer-inline')
  $UserCopyWith<$Res> get patient {
    return $UserCopyWith<$Res>(_value.patient, (value) {
      return _then(_value.copyWith(patient: value) as $Val);
    });
  }
}

/// @nodoc
abstract class _$$AppointmentImplCopyWith<$Res>
    implements $AppointmentCopyWith<$Res> {
  factory _$$AppointmentImplCopyWith(
          _$AppointmentImpl value, $Res Function(_$AppointmentImpl) then) =
      __$$AppointmentImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call(
      {int id,
      DateTime dateTime,
      String status,
      User doctor,
      User patient,
      String? notes,
      String? diagnosis,
      String? prescription,
      DateTime? createdAt,
      DateTime? updatedAt});

  @override
  $UserCopyWith<$Res> get doctor;
  @override
  $UserCopyWith<$Res> get patient;
}

/// @nodoc
class __$$AppointmentImplCopyWithImpl<$Res>
    extends _$AppointmentCopyWithImpl<$Res, _$AppointmentImpl>
    implements _$$AppointmentImplCopyWith<$Res> {
  __$$AppointmentImplCopyWithImpl(
      _$AppointmentImpl _value, $Res Function(_$AppointmentImpl) _then)
      : super(_value, _then);

  /// Create a copy of Appointment
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? dateTime = null,
    Object? status = null,
    Object? doctor = null,
    Object? patient = null,
    Object? notes = freezed,
    Object? diagnosis = freezed,
    Object? prescription = freezed,
    Object? createdAt = freezed,
    Object? updatedAt = freezed,
  }) {
    return _then(_$AppointmentImpl(
      id: null == id
          ? _value.id
          : id // ignore: cast_nullable_to_non_nullable
              as int,
      dateTime: null == dateTime
          ? _value.dateTime
          : dateTime // ignore: cast_nullable_to_non_nullable
              as DateTime,
      status: null == status
          ? _value.status
          : status // ignore: cast_nullable_to_non_nullable
              as String,
      doctor: null == doctor
          ? _value.doctor
          : doctor // ignore: cast_nullable_to_non_nullable
              as User,
      patient: null == patient
          ? _value.patient
          : patient // ignore: cast_nullable_to_non_nullable
              as User,
      notes: freezed == notes
          ? _value.notes
          : notes // ignore: cast_nullable_to_non_nullable
              as String?,
      diagnosis: freezed == diagnosis
          ? _value.diagnosis
          : diagnosis // ignore: cast_nullable_to_non_nullable
              as String?,
      prescription: freezed == prescription
          ? _value.prescription
          : prescription // ignore: cast_nullable_to_non_nullable
              as String?,
      createdAt: freezed == createdAt
          ? _value.createdAt
          : createdAt // ignore: cast_nullable_to_non_nullable
              as DateTime?,
      updatedAt: freezed == updatedAt
          ? _value.updatedAt
          : updatedAt // ignore: cast_nullable_to_non_nullable
              as DateTime?,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$AppointmentImpl implements _Appointment {
  const _$AppointmentImpl(
      {required this.id,
      required this.dateTime,
      required this.status,
      required this.doctor,
      required this.patient,
      this.notes,
      this.diagnosis,
      this.prescription,
      this.createdAt,
      this.updatedAt});

  factory _$AppointmentImpl.fromJson(Map<String, dynamic> json) =>
      _$$AppointmentImplFromJson(json);

  @override
  final int id;
  @override
  final DateTime dateTime;
  @override
  final String status;
  @override
  final User doctor;
  @override
  final User patient;
  @override
  final String? notes;
  @override
  final String? diagnosis;
  @override
  final String? prescription;
  @override
  final DateTime? createdAt;
  @override
  final DateTime? updatedAt;

  @override
  String toString() {
    return 'Appointment(id: $id, dateTime: $dateTime, status: $status, doctor: $doctor, patient: $patient, notes: $notes, diagnosis: $diagnosis, prescription: $prescription, createdAt: $createdAt, updatedAt: $updatedAt)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$AppointmentImpl &&
            (identical(other.id, id) || other.id == id) &&
            (identical(other.dateTime, dateTime) ||
                other.dateTime == dateTime) &&
            (identical(other.status, status) || other.status == status) &&
            (identical(other.doctor, doctor) || other.doctor == doctor) &&
            (identical(other.patient, patient) || other.patient == patient) &&
            (identical(other.notes, notes) || other.notes == notes) &&
            (identical(other.diagnosis, diagnosis) ||
                other.diagnosis == diagnosis) &&
            (identical(other.prescription, prescription) ||
                other.prescription == prescription) &&
            (identical(other.createdAt, createdAt) ||
                other.createdAt == createdAt) &&
            (identical(other.updatedAt, updatedAt) ||
                other.updatedAt == updatedAt));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(runtimeType, id, dateTime, status, doctor,
      patient, notes, diagnosis, prescription, createdAt, updatedAt);

  /// Create a copy of Appointment
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$AppointmentImplCopyWith<_$AppointmentImpl> get copyWith =>
      __$$AppointmentImplCopyWithImpl<_$AppointmentImpl>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$AppointmentImplToJson(
      this,
    );
  }
}

abstract class _Appointment implements Appointment {
  const factory _Appointment(
      {required final int id,
      required final DateTime dateTime,
      required final String status,
      required final User doctor,
      required final User patient,
      final String? notes,
      final String? diagnosis,
      final String? prescription,
      final DateTime? createdAt,
      final DateTime? updatedAt}) = _$AppointmentImpl;

  factory _Appointment.fromJson(Map<String, dynamic> json) =
      _$AppointmentImpl.fromJson;

  @override
  int get id;
  @override
  DateTime get dateTime;
  @override
  String get status;
  @override
  User get doctor;
  @override
  User get patient;
  @override
  String? get notes;
  @override
  String? get diagnosis;
  @override
  String? get prescription;
  @override
  DateTime? get createdAt;
  @override
  DateTime? get updatedAt;

  /// Create a copy of Appointment
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$AppointmentImplCopyWith<_$AppointmentImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

AppointmentRequest _$AppointmentRequestFromJson(Map<String, dynamic> json) {
  return _AppointmentRequest.fromJson(json);
}

/// @nodoc
mixin _$AppointmentRequest {
  int get doctorId => throw _privateConstructorUsedError;
  DateTime get dateTime => throw _privateConstructorUsedError;
  String? get notes => throw _privateConstructorUsedError;

  /// Serializes this AppointmentRequest to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of AppointmentRequest
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $AppointmentRequestCopyWith<AppointmentRequest> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $AppointmentRequestCopyWith<$Res> {
  factory $AppointmentRequestCopyWith(
          AppointmentRequest value, $Res Function(AppointmentRequest) then) =
      _$AppointmentRequestCopyWithImpl<$Res, AppointmentRequest>;
  @useResult
  $Res call({int doctorId, DateTime dateTime, String? notes});
}

/// @nodoc
class _$AppointmentRequestCopyWithImpl<$Res, $Val extends AppointmentRequest>
    implements $AppointmentRequestCopyWith<$Res> {
  _$AppointmentRequestCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of AppointmentRequest
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? doctorId = null,
    Object? dateTime = null,
    Object? notes = freezed,
  }) {
    return _then(_value.copyWith(
      doctorId: null == doctorId
          ? _value.doctorId
          : doctorId // ignore: cast_nullable_to_non_nullable
              as int,
      dateTime: null == dateTime
          ? _value.dateTime
          : dateTime // ignore: cast_nullable_to_non_nullable
              as DateTime,
      notes: freezed == notes
          ? _value.notes
          : notes // ignore: cast_nullable_to_non_nullable
              as String?,
    ) as $Val);
  }
}

/// @nodoc
abstract class _$$AppointmentRequestImplCopyWith<$Res>
    implements $AppointmentRequestCopyWith<$Res> {
  factory _$$AppointmentRequestImplCopyWith(_$AppointmentRequestImpl value,
          $Res Function(_$AppointmentRequestImpl) then) =
      __$$AppointmentRequestImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({int doctorId, DateTime dateTime, String? notes});
}

/// @nodoc
class __$$AppointmentRequestImplCopyWithImpl<$Res>
    extends _$AppointmentRequestCopyWithImpl<$Res, _$AppointmentRequestImpl>
    implements _$$AppointmentRequestImplCopyWith<$Res> {
  __$$AppointmentRequestImplCopyWithImpl(_$AppointmentRequestImpl _value,
      $Res Function(_$AppointmentRequestImpl) _then)
      : super(_value, _then);

  /// Create a copy of AppointmentRequest
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? doctorId = null,
    Object? dateTime = null,
    Object? notes = freezed,
  }) {
    return _then(_$AppointmentRequestImpl(
      doctorId: null == doctorId
          ? _value.doctorId
          : doctorId // ignore: cast_nullable_to_non_nullable
              as int,
      dateTime: null == dateTime
          ? _value.dateTime
          : dateTime // ignore: cast_nullable_to_non_nullable
              as DateTime,
      notes: freezed == notes
          ? _value.notes
          : notes // ignore: cast_nullable_to_non_nullable
              as String?,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$AppointmentRequestImpl implements _AppointmentRequest {
  const _$AppointmentRequestImpl(
      {required this.doctorId, required this.dateTime, this.notes});

  factory _$AppointmentRequestImpl.fromJson(Map<String, dynamic> json) =>
      _$$AppointmentRequestImplFromJson(json);

  @override
  final int doctorId;
  @override
  final DateTime dateTime;
  @override
  final String? notes;

  @override
  String toString() {
    return 'AppointmentRequest(doctorId: $doctorId, dateTime: $dateTime, notes: $notes)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$AppointmentRequestImpl &&
            (identical(other.doctorId, doctorId) ||
                other.doctorId == doctorId) &&
            (identical(other.dateTime, dateTime) ||
                other.dateTime == dateTime) &&
            (identical(other.notes, notes) || other.notes == notes));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(runtimeType, doctorId, dateTime, notes);

  /// Create a copy of AppointmentRequest
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$AppointmentRequestImplCopyWith<_$AppointmentRequestImpl> get copyWith =>
      __$$AppointmentRequestImplCopyWithImpl<_$AppointmentRequestImpl>(
          this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$AppointmentRequestImplToJson(
      this,
    );
  }
}

abstract class _AppointmentRequest implements AppointmentRequest {
  const factory _AppointmentRequest(
      {required final int doctorId,
      required final DateTime dateTime,
      final String? notes}) = _$AppointmentRequestImpl;

  factory _AppointmentRequest.fromJson(Map<String, dynamic> json) =
      _$AppointmentRequestImpl.fromJson;

  @override
  int get doctorId;
  @override
  DateTime get dateTime;
  @override
  String? get notes;

  /// Create a copy of AppointmentRequest
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$AppointmentRequestImplCopyWith<_$AppointmentRequestImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

AppointmentSlot _$AppointmentSlotFromJson(Map<String, dynamic> json) {
  return _AppointmentSlot.fromJson(json);
}

/// @nodoc
mixin _$AppointmentSlot {
  DateTime get dateTime => throw _privateConstructorUsedError;
  bool get isAvailable => throw _privateConstructorUsedError;
  int? get appointmentId => throw _privateConstructorUsedError;

  /// Serializes this AppointmentSlot to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of AppointmentSlot
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $AppointmentSlotCopyWith<AppointmentSlot> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $AppointmentSlotCopyWith<$Res> {
  factory $AppointmentSlotCopyWith(
          AppointmentSlot value, $Res Function(AppointmentSlot) then) =
      _$AppointmentSlotCopyWithImpl<$Res, AppointmentSlot>;
  @useResult
  $Res call({DateTime dateTime, bool isAvailable, int? appointmentId});
}

/// @nodoc
class _$AppointmentSlotCopyWithImpl<$Res, $Val extends AppointmentSlot>
    implements $AppointmentSlotCopyWith<$Res> {
  _$AppointmentSlotCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of AppointmentSlot
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? dateTime = null,
    Object? isAvailable = null,
    Object? appointmentId = freezed,
  }) {
    return _then(_value.copyWith(
      dateTime: null == dateTime
          ? _value.dateTime
          : dateTime // ignore: cast_nullable_to_non_nullable
              as DateTime,
      isAvailable: null == isAvailable
          ? _value.isAvailable
          : isAvailable // ignore: cast_nullable_to_non_nullable
              as bool,
      appointmentId: freezed == appointmentId
          ? _value.appointmentId
          : appointmentId // ignore: cast_nullable_to_non_nullable
              as int?,
    ) as $Val);
  }
}

/// @nodoc
abstract class _$$AppointmentSlotImplCopyWith<$Res>
    implements $AppointmentSlotCopyWith<$Res> {
  factory _$$AppointmentSlotImplCopyWith(_$AppointmentSlotImpl value,
          $Res Function(_$AppointmentSlotImpl) then) =
      __$$AppointmentSlotImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({DateTime dateTime, bool isAvailable, int? appointmentId});
}

/// @nodoc
class __$$AppointmentSlotImplCopyWithImpl<$Res>
    extends _$AppointmentSlotCopyWithImpl<$Res, _$AppointmentSlotImpl>
    implements _$$AppointmentSlotImplCopyWith<$Res> {
  __$$AppointmentSlotImplCopyWithImpl(
      _$AppointmentSlotImpl _value, $Res Function(_$AppointmentSlotImpl) _then)
      : super(_value, _then);

  /// Create a copy of AppointmentSlot
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? dateTime = null,
    Object? isAvailable = null,
    Object? appointmentId = freezed,
  }) {
    return _then(_$AppointmentSlotImpl(
      dateTime: null == dateTime
          ? _value.dateTime
          : dateTime // ignore: cast_nullable_to_non_nullable
              as DateTime,
      isAvailable: null == isAvailable
          ? _value.isAvailable
          : isAvailable // ignore: cast_nullable_to_non_nullable
              as bool,
      appointmentId: freezed == appointmentId
          ? _value.appointmentId
          : appointmentId // ignore: cast_nullable_to_non_nullable
              as int?,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$AppointmentSlotImpl implements _AppointmentSlot {
  const _$AppointmentSlotImpl(
      {required this.dateTime, required this.isAvailable, this.appointmentId});

  factory _$AppointmentSlotImpl.fromJson(Map<String, dynamic> json) =>
      _$$AppointmentSlotImplFromJson(json);

  @override
  final DateTime dateTime;
  @override
  final bool isAvailable;
  @override
  final int? appointmentId;

  @override
  String toString() {
    return 'AppointmentSlot(dateTime: $dateTime, isAvailable: $isAvailable, appointmentId: $appointmentId)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$AppointmentSlotImpl &&
            (identical(other.dateTime, dateTime) ||
                other.dateTime == dateTime) &&
            (identical(other.isAvailable, isAvailable) ||
                other.isAvailable == isAvailable) &&
            (identical(other.appointmentId, appointmentId) ||
                other.appointmentId == appointmentId));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode =>
      Object.hash(runtimeType, dateTime, isAvailable, appointmentId);

  /// Create a copy of AppointmentSlot
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$AppointmentSlotImplCopyWith<_$AppointmentSlotImpl> get copyWith =>
      __$$AppointmentSlotImplCopyWithImpl<_$AppointmentSlotImpl>(
          this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$AppointmentSlotImplToJson(
      this,
    );
  }
}

abstract class _AppointmentSlot implements AppointmentSlot {
  const factory _AppointmentSlot(
      {required final DateTime dateTime,
      required final bool isAvailable,
      final int? appointmentId}) = _$AppointmentSlotImpl;

  factory _AppointmentSlot.fromJson(Map<String, dynamic> json) =
      _$AppointmentSlotImpl.fromJson;

  @override
  DateTime get dateTime;
  @override
  bool get isAvailable;
  @override
  int? get appointmentId;

  /// Create a copy of AppointmentSlot
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$AppointmentSlotImplCopyWith<_$AppointmentSlotImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

Doctor _$DoctorFromJson(Map<String, dynamic> json) {
  return _Doctor.fromJson(json);
}

/// @nodoc
mixin _$Doctor {
  int get id => throw _privateConstructorUsedError;
  String get firstName => throw _privateConstructorUsedError;
  String get lastName => throw _privateConstructorUsedError;
  String get specialty => throw _privateConstructorUsedError;
  String? get phone => throw _privateConstructorUsedError;
  String? get email => throw _privateConstructorUsedError;
  String? get address => throw _privateConstructorUsedError;
  double? get rating => throw _privateConstructorUsedError;
  List<AppointmentSlot>? get availableSlots =>
      throw _privateConstructorUsedError;

  /// Serializes this Doctor to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of Doctor
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $DoctorCopyWith<Doctor> get copyWith => throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $DoctorCopyWith<$Res> {
  factory $DoctorCopyWith(Doctor value, $Res Function(Doctor) then) =
      _$DoctorCopyWithImpl<$Res, Doctor>;
  @useResult
  $Res call(
      {int id,
      String firstName,
      String lastName,
      String specialty,
      String? phone,
      String? email,
      String? address,
      double? rating,
      List<AppointmentSlot>? availableSlots});
}

/// @nodoc
class _$DoctorCopyWithImpl<$Res, $Val extends Doctor>
    implements $DoctorCopyWith<$Res> {
  _$DoctorCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of Doctor
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? firstName = null,
    Object? lastName = null,
    Object? specialty = null,
    Object? phone = freezed,
    Object? email = freezed,
    Object? address = freezed,
    Object? rating = freezed,
    Object? availableSlots = freezed,
  }) {
    return _then(_value.copyWith(
      id: null == id
          ? _value.id
          : id // ignore: cast_nullable_to_non_nullable
              as int,
      firstName: null == firstName
          ? _value.firstName
          : firstName // ignore: cast_nullable_to_non_nullable
              as String,
      lastName: null == lastName
          ? _value.lastName
          : lastName // ignore: cast_nullable_to_non_nullable
              as String,
      specialty: null == specialty
          ? _value.specialty
          : specialty // ignore: cast_nullable_to_non_nullable
              as String,
      phone: freezed == phone
          ? _value.phone
          : phone // ignore: cast_nullable_to_non_nullable
              as String?,
      email: freezed == email
          ? _value.email
          : email // ignore: cast_nullable_to_non_nullable
              as String?,
      address: freezed == address
          ? _value.address
          : address // ignore: cast_nullable_to_non_nullable
              as String?,
      rating: freezed == rating
          ? _value.rating
          : rating // ignore: cast_nullable_to_non_nullable
              as double?,
      availableSlots: freezed == availableSlots
          ? _value.availableSlots
          : availableSlots // ignore: cast_nullable_to_non_nullable
              as List<AppointmentSlot>?,
    ) as $Val);
  }
}

/// @nodoc
abstract class _$$DoctorImplCopyWith<$Res> implements $DoctorCopyWith<$Res> {
  factory _$$DoctorImplCopyWith(
          _$DoctorImpl value, $Res Function(_$DoctorImpl) then) =
      __$$DoctorImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call(
      {int id,
      String firstName,
      String lastName,
      String specialty,
      String? phone,
      String? email,
      String? address,
      double? rating,
      List<AppointmentSlot>? availableSlots});
}

/// @nodoc
class __$$DoctorImplCopyWithImpl<$Res>
    extends _$DoctorCopyWithImpl<$Res, _$DoctorImpl>
    implements _$$DoctorImplCopyWith<$Res> {
  __$$DoctorImplCopyWithImpl(
      _$DoctorImpl _value, $Res Function(_$DoctorImpl) _then)
      : super(_value, _then);

  /// Create a copy of Doctor
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? firstName = null,
    Object? lastName = null,
    Object? specialty = null,
    Object? phone = freezed,
    Object? email = freezed,
    Object? address = freezed,
    Object? rating = freezed,
    Object? availableSlots = freezed,
  }) {
    return _then(_$DoctorImpl(
      id: null == id
          ? _value.id
          : id // ignore: cast_nullable_to_non_nullable
              as int,
      firstName: null == firstName
          ? _value.firstName
          : firstName // ignore: cast_nullable_to_non_nullable
              as String,
      lastName: null == lastName
          ? _value.lastName
          : lastName // ignore: cast_nullable_to_non_nullable
              as String,
      specialty: null == specialty
          ? _value.specialty
          : specialty // ignore: cast_nullable_to_non_nullable
              as String,
      phone: freezed == phone
          ? _value.phone
          : phone // ignore: cast_nullable_to_non_nullable
              as String?,
      email: freezed == email
          ? _value.email
          : email // ignore: cast_nullable_to_non_nullable
              as String?,
      address: freezed == address
          ? _value.address
          : address // ignore: cast_nullable_to_non_nullable
              as String?,
      rating: freezed == rating
          ? _value.rating
          : rating // ignore: cast_nullable_to_non_nullable
              as double?,
      availableSlots: freezed == availableSlots
          ? _value._availableSlots
          : availableSlots // ignore: cast_nullable_to_non_nullable
              as List<AppointmentSlot>?,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$DoctorImpl implements _Doctor {
  const _$DoctorImpl(
      {required this.id,
      required this.firstName,
      required this.lastName,
      required this.specialty,
      this.phone,
      this.email,
      this.address,
      this.rating,
      final List<AppointmentSlot>? availableSlots})
      : _availableSlots = availableSlots;

  factory _$DoctorImpl.fromJson(Map<String, dynamic> json) =>
      _$$DoctorImplFromJson(json);

  @override
  final int id;
  @override
  final String firstName;
  @override
  final String lastName;
  @override
  final String specialty;
  @override
  final String? phone;
  @override
  final String? email;
  @override
  final String? address;
  @override
  final double? rating;
  final List<AppointmentSlot>? _availableSlots;
  @override
  List<AppointmentSlot>? get availableSlots {
    final value = _availableSlots;
    if (value == null) return null;
    if (_availableSlots is EqualUnmodifiableListView) return _availableSlots;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(value);
  }

  @override
  String toString() {
    return 'Doctor(id: $id, firstName: $firstName, lastName: $lastName, specialty: $specialty, phone: $phone, email: $email, address: $address, rating: $rating, availableSlots: $availableSlots)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$DoctorImpl &&
            (identical(other.id, id) || other.id == id) &&
            (identical(other.firstName, firstName) ||
                other.firstName == firstName) &&
            (identical(other.lastName, lastName) ||
                other.lastName == lastName) &&
            (identical(other.specialty, specialty) ||
                other.specialty == specialty) &&
            (identical(other.phone, phone) || other.phone == phone) &&
            (identical(other.email, email) || other.email == email) &&
            (identical(other.address, address) || other.address == address) &&
            (identical(other.rating, rating) || other.rating == rating) &&
            const DeepCollectionEquality()
                .equals(other._availableSlots, _availableSlots));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
      runtimeType,
      id,
      firstName,
      lastName,
      specialty,
      phone,
      email,
      address,
      rating,
      const DeepCollectionEquality().hash(_availableSlots));

  /// Create a copy of Doctor
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$DoctorImplCopyWith<_$DoctorImpl> get copyWith =>
      __$$DoctorImplCopyWithImpl<_$DoctorImpl>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$DoctorImplToJson(
      this,
    );
  }
}

abstract class _Doctor implements Doctor {
  const factory _Doctor(
      {required final int id,
      required final String firstName,
      required final String lastName,
      required final String specialty,
      final String? phone,
      final String? email,
      final String? address,
      final double? rating,
      final List<AppointmentSlot>? availableSlots}) = _$DoctorImpl;

  factory _Doctor.fromJson(Map<String, dynamic> json) = _$DoctorImpl.fromJson;

  @override
  int get id;
  @override
  String get firstName;
  @override
  String get lastName;
  @override
  String get specialty;
  @override
  String? get phone;
  @override
  String? get email;
  @override
  String? get address;
  @override
  double? get rating;
  @override
  List<AppointmentSlot>? get availableSlots;

  /// Create a copy of Doctor
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$DoctorImplCopyWith<_$DoctorImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
