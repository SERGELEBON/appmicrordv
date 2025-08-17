// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'chat.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models');

ChatMessage _$ChatMessageFromJson(Map<String, dynamic> json) {
  return _ChatMessage.fromJson(json);
}

/// @nodoc
mixin _$ChatMessage {
  String get id => throw _privateConstructorUsedError;
  String get content => throw _privateConstructorUsedError;
  String get role =>
      throw _privateConstructorUsedError; // 'user' or 'assistant'
  DateTime get timestamp => throw _privateConstructorUsedError;
  String? get type =>
      throw _privateConstructorUsedError; // 'text', 'suggestion', 'diagnosis'
  Map<String, dynamic>? get metadata => throw _privateConstructorUsedError;

  /// Serializes this ChatMessage to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of ChatMessage
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $ChatMessageCopyWith<ChatMessage> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $ChatMessageCopyWith<$Res> {
  factory $ChatMessageCopyWith(
          ChatMessage value, $Res Function(ChatMessage) then) =
      _$ChatMessageCopyWithImpl<$Res, ChatMessage>;
  @useResult
  $Res call(
      {String id,
      String content,
      String role,
      DateTime timestamp,
      String? type,
      Map<String, dynamic>? metadata});
}

/// @nodoc
class _$ChatMessageCopyWithImpl<$Res, $Val extends ChatMessage>
    implements $ChatMessageCopyWith<$Res> {
  _$ChatMessageCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of ChatMessage
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? content = null,
    Object? role = null,
    Object? timestamp = null,
    Object? type = freezed,
    Object? metadata = freezed,
  }) {
    return _then(_value.copyWith(
      id: null == id
          ? _value.id
          : id // ignore: cast_nullable_to_non_nullable
              as String,
      content: null == content
          ? _value.content
          : content // ignore: cast_nullable_to_non_nullable
              as String,
      role: null == role
          ? _value.role
          : role // ignore: cast_nullable_to_non_nullable
              as String,
      timestamp: null == timestamp
          ? _value.timestamp
          : timestamp // ignore: cast_nullable_to_non_nullable
              as DateTime,
      type: freezed == type
          ? _value.type
          : type // ignore: cast_nullable_to_non_nullable
              as String?,
      metadata: freezed == metadata
          ? _value.metadata
          : metadata // ignore: cast_nullable_to_non_nullable
              as Map<String, dynamic>?,
    ) as $Val);
  }
}

/// @nodoc
abstract class _$$ChatMessageImplCopyWith<$Res>
    implements $ChatMessageCopyWith<$Res> {
  factory _$$ChatMessageImplCopyWith(
          _$ChatMessageImpl value, $Res Function(_$ChatMessageImpl) then) =
      __$$ChatMessageImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call(
      {String id,
      String content,
      String role,
      DateTime timestamp,
      String? type,
      Map<String, dynamic>? metadata});
}

/// @nodoc
class __$$ChatMessageImplCopyWithImpl<$Res>
    extends _$ChatMessageCopyWithImpl<$Res, _$ChatMessageImpl>
    implements _$$ChatMessageImplCopyWith<$Res> {
  __$$ChatMessageImplCopyWithImpl(
      _$ChatMessageImpl _value, $Res Function(_$ChatMessageImpl) _then)
      : super(_value, _then);

  /// Create a copy of ChatMessage
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? content = null,
    Object? role = null,
    Object? timestamp = null,
    Object? type = freezed,
    Object? metadata = freezed,
  }) {
    return _then(_$ChatMessageImpl(
      id: null == id
          ? _value.id
          : id // ignore: cast_nullable_to_non_nullable
              as String,
      content: null == content
          ? _value.content
          : content // ignore: cast_nullable_to_non_nullable
              as String,
      role: null == role
          ? _value.role
          : role // ignore: cast_nullable_to_non_nullable
              as String,
      timestamp: null == timestamp
          ? _value.timestamp
          : timestamp // ignore: cast_nullable_to_non_nullable
              as DateTime,
      type: freezed == type
          ? _value.type
          : type // ignore: cast_nullable_to_non_nullable
              as String?,
      metadata: freezed == metadata
          ? _value._metadata
          : metadata // ignore: cast_nullable_to_non_nullable
              as Map<String, dynamic>?,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$ChatMessageImpl implements _ChatMessage {
  const _$ChatMessageImpl(
      {required this.id,
      required this.content,
      required this.role,
      required this.timestamp,
      this.type,
      final Map<String, dynamic>? metadata})
      : _metadata = metadata;

  factory _$ChatMessageImpl.fromJson(Map<String, dynamic> json) =>
      _$$ChatMessageImplFromJson(json);

  @override
  final String id;
  @override
  final String content;
  @override
  final String role;
// 'user' or 'assistant'
  @override
  final DateTime timestamp;
  @override
  final String? type;
// 'text', 'suggestion', 'diagnosis'
  final Map<String, dynamic>? _metadata;
// 'text', 'suggestion', 'diagnosis'
  @override
  Map<String, dynamic>? get metadata {
    final value = _metadata;
    if (value == null) return null;
    if (_metadata is EqualUnmodifiableMapView) return _metadata;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableMapView(value);
  }

  @override
  String toString() {
    return 'ChatMessage(id: $id, content: $content, role: $role, timestamp: $timestamp, type: $type, metadata: $metadata)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$ChatMessageImpl &&
            (identical(other.id, id) || other.id == id) &&
            (identical(other.content, content) || other.content == content) &&
            (identical(other.role, role) || other.role == role) &&
            (identical(other.timestamp, timestamp) ||
                other.timestamp == timestamp) &&
            (identical(other.type, type) || other.type == type) &&
            const DeepCollectionEquality().equals(other._metadata, _metadata));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(runtimeType, id, content, role, timestamp,
      type, const DeepCollectionEquality().hash(_metadata));

  /// Create a copy of ChatMessage
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$ChatMessageImplCopyWith<_$ChatMessageImpl> get copyWith =>
      __$$ChatMessageImplCopyWithImpl<_$ChatMessageImpl>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$ChatMessageImplToJson(
      this,
    );
  }
}

abstract class _ChatMessage implements ChatMessage {
  const factory _ChatMessage(
      {required final String id,
      required final String content,
      required final String role,
      required final DateTime timestamp,
      final String? type,
      final Map<String, dynamic>? metadata}) = _$ChatMessageImpl;

  factory _ChatMessage.fromJson(Map<String, dynamic> json) =
      _$ChatMessageImpl.fromJson;

  @override
  String get id;
  @override
  String get content;
  @override
  String get role; // 'user' or 'assistant'
  @override
  DateTime get timestamp;
  @override
  String? get type; // 'text', 'suggestion', 'diagnosis'
  @override
  Map<String, dynamic>? get metadata;

  /// Create a copy of ChatMessage
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$ChatMessageImplCopyWith<_$ChatMessageImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

ChatRequest _$ChatRequestFromJson(Map<String, dynamic> json) {
  return _ChatRequest.fromJson(json);
}

/// @nodoc
mixin _$ChatRequest {
  String get message => throw _privateConstructorUsedError;
  String? get conversationId => throw _privateConstructorUsedError;
  String? get context => throw _privateConstructorUsedError;

  /// Serializes this ChatRequest to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of ChatRequest
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $ChatRequestCopyWith<ChatRequest> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $ChatRequestCopyWith<$Res> {
  factory $ChatRequestCopyWith(
          ChatRequest value, $Res Function(ChatRequest) then) =
      _$ChatRequestCopyWithImpl<$Res, ChatRequest>;
  @useResult
  $Res call({String message, String? conversationId, String? context});
}

/// @nodoc
class _$ChatRequestCopyWithImpl<$Res, $Val extends ChatRequest>
    implements $ChatRequestCopyWith<$Res> {
  _$ChatRequestCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of ChatRequest
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? message = null,
    Object? conversationId = freezed,
    Object? context = freezed,
  }) {
    return _then(_value.copyWith(
      message: null == message
          ? _value.message
          : message // ignore: cast_nullable_to_non_nullable
              as String,
      conversationId: freezed == conversationId
          ? _value.conversationId
          : conversationId // ignore: cast_nullable_to_non_nullable
              as String?,
      context: freezed == context
          ? _value.context
          : context // ignore: cast_nullable_to_non_nullable
              as String?,
    ) as $Val);
  }
}

/// @nodoc
abstract class _$$ChatRequestImplCopyWith<$Res>
    implements $ChatRequestCopyWith<$Res> {
  factory _$$ChatRequestImplCopyWith(
          _$ChatRequestImpl value, $Res Function(_$ChatRequestImpl) then) =
      __$$ChatRequestImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({String message, String? conversationId, String? context});
}

/// @nodoc
class __$$ChatRequestImplCopyWithImpl<$Res>
    extends _$ChatRequestCopyWithImpl<$Res, _$ChatRequestImpl>
    implements _$$ChatRequestImplCopyWith<$Res> {
  __$$ChatRequestImplCopyWithImpl(
      _$ChatRequestImpl _value, $Res Function(_$ChatRequestImpl) _then)
      : super(_value, _then);

  /// Create a copy of ChatRequest
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? message = null,
    Object? conversationId = freezed,
    Object? context = freezed,
  }) {
    return _then(_$ChatRequestImpl(
      message: null == message
          ? _value.message
          : message // ignore: cast_nullable_to_non_nullable
              as String,
      conversationId: freezed == conversationId
          ? _value.conversationId
          : conversationId // ignore: cast_nullable_to_non_nullable
              as String?,
      context: freezed == context
          ? _value.context
          : context // ignore: cast_nullable_to_non_nullable
              as String?,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$ChatRequestImpl implements _ChatRequest {
  const _$ChatRequestImpl(
      {required this.message, this.conversationId, this.context});

  factory _$ChatRequestImpl.fromJson(Map<String, dynamic> json) =>
      _$$ChatRequestImplFromJson(json);

  @override
  final String message;
  @override
  final String? conversationId;
  @override
  final String? context;

  @override
  String toString() {
    return 'ChatRequest(message: $message, conversationId: $conversationId, context: $context)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$ChatRequestImpl &&
            (identical(other.message, message) || other.message == message) &&
            (identical(other.conversationId, conversationId) ||
                other.conversationId == conversationId) &&
            (identical(other.context, context) || other.context == context));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode =>
      Object.hash(runtimeType, message, conversationId, context);

  /// Create a copy of ChatRequest
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$ChatRequestImplCopyWith<_$ChatRequestImpl> get copyWith =>
      __$$ChatRequestImplCopyWithImpl<_$ChatRequestImpl>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$ChatRequestImplToJson(
      this,
    );
  }
}

abstract class _ChatRequest implements ChatRequest {
  const factory _ChatRequest(
      {required final String message,
      final String? conversationId,
      final String? context}) = _$ChatRequestImpl;

  factory _ChatRequest.fromJson(Map<String, dynamic> json) =
      _$ChatRequestImpl.fromJson;

  @override
  String get message;
  @override
  String? get conversationId;
  @override
  String? get context;

  /// Create a copy of ChatRequest
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$ChatRequestImplCopyWith<_$ChatRequestImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

ChatResponse _$ChatResponseFromJson(Map<String, dynamic> json) {
  return _ChatResponse.fromJson(json);
}

/// @nodoc
mixin _$ChatResponse {
  String get message => throw _privateConstructorUsedError;
  String get conversationId => throw _privateConstructorUsedError;
  String? get suggestedSpecialty => throw _privateConstructorUsedError;
  List<String>? get suggestions => throw _privateConstructorUsedError;
  Map<String, dynamic>? get aiMetadata => throw _privateConstructorUsedError;

  /// Serializes this ChatResponse to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of ChatResponse
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $ChatResponseCopyWith<ChatResponse> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $ChatResponseCopyWith<$Res> {
  factory $ChatResponseCopyWith(
          ChatResponse value, $Res Function(ChatResponse) then) =
      _$ChatResponseCopyWithImpl<$Res, ChatResponse>;
  @useResult
  $Res call(
      {String message,
      String conversationId,
      String? suggestedSpecialty,
      List<String>? suggestions,
      Map<String, dynamic>? aiMetadata});
}

/// @nodoc
class _$ChatResponseCopyWithImpl<$Res, $Val extends ChatResponse>
    implements $ChatResponseCopyWith<$Res> {
  _$ChatResponseCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of ChatResponse
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? message = null,
    Object? conversationId = null,
    Object? suggestedSpecialty = freezed,
    Object? suggestions = freezed,
    Object? aiMetadata = freezed,
  }) {
    return _then(_value.copyWith(
      message: null == message
          ? _value.message
          : message // ignore: cast_nullable_to_non_nullable
              as String,
      conversationId: null == conversationId
          ? _value.conversationId
          : conversationId // ignore: cast_nullable_to_non_nullable
              as String,
      suggestedSpecialty: freezed == suggestedSpecialty
          ? _value.suggestedSpecialty
          : suggestedSpecialty // ignore: cast_nullable_to_non_nullable
              as String?,
      suggestions: freezed == suggestions
          ? _value.suggestions
          : suggestions // ignore: cast_nullable_to_non_nullable
              as List<String>?,
      aiMetadata: freezed == aiMetadata
          ? _value.aiMetadata
          : aiMetadata // ignore: cast_nullable_to_non_nullable
              as Map<String, dynamic>?,
    ) as $Val);
  }
}

/// @nodoc
abstract class _$$ChatResponseImplCopyWith<$Res>
    implements $ChatResponseCopyWith<$Res> {
  factory _$$ChatResponseImplCopyWith(
          _$ChatResponseImpl value, $Res Function(_$ChatResponseImpl) then) =
      __$$ChatResponseImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call(
      {String message,
      String conversationId,
      String? suggestedSpecialty,
      List<String>? suggestions,
      Map<String, dynamic>? aiMetadata});
}

/// @nodoc
class __$$ChatResponseImplCopyWithImpl<$Res>
    extends _$ChatResponseCopyWithImpl<$Res, _$ChatResponseImpl>
    implements _$$ChatResponseImplCopyWith<$Res> {
  __$$ChatResponseImplCopyWithImpl(
      _$ChatResponseImpl _value, $Res Function(_$ChatResponseImpl) _then)
      : super(_value, _then);

  /// Create a copy of ChatResponse
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? message = null,
    Object? conversationId = null,
    Object? suggestedSpecialty = freezed,
    Object? suggestions = freezed,
    Object? aiMetadata = freezed,
  }) {
    return _then(_$ChatResponseImpl(
      message: null == message
          ? _value.message
          : message // ignore: cast_nullable_to_non_nullable
              as String,
      conversationId: null == conversationId
          ? _value.conversationId
          : conversationId // ignore: cast_nullable_to_non_nullable
              as String,
      suggestedSpecialty: freezed == suggestedSpecialty
          ? _value.suggestedSpecialty
          : suggestedSpecialty // ignore: cast_nullable_to_non_nullable
              as String?,
      suggestions: freezed == suggestions
          ? _value._suggestions
          : suggestions // ignore: cast_nullable_to_non_nullable
              as List<String>?,
      aiMetadata: freezed == aiMetadata
          ? _value._aiMetadata
          : aiMetadata // ignore: cast_nullable_to_non_nullable
              as Map<String, dynamic>?,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$ChatResponseImpl implements _ChatResponse {
  const _$ChatResponseImpl(
      {required this.message,
      required this.conversationId,
      this.suggestedSpecialty,
      final List<String>? suggestions,
      final Map<String, dynamic>? aiMetadata})
      : _suggestions = suggestions,
        _aiMetadata = aiMetadata;

  factory _$ChatResponseImpl.fromJson(Map<String, dynamic> json) =>
      _$$ChatResponseImplFromJson(json);

  @override
  final String message;
  @override
  final String conversationId;
  @override
  final String? suggestedSpecialty;
  final List<String>? _suggestions;
  @override
  List<String>? get suggestions {
    final value = _suggestions;
    if (value == null) return null;
    if (_suggestions is EqualUnmodifiableListView) return _suggestions;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(value);
  }

  final Map<String, dynamic>? _aiMetadata;
  @override
  Map<String, dynamic>? get aiMetadata {
    final value = _aiMetadata;
    if (value == null) return null;
    if (_aiMetadata is EqualUnmodifiableMapView) return _aiMetadata;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableMapView(value);
  }

  @override
  String toString() {
    return 'ChatResponse(message: $message, conversationId: $conversationId, suggestedSpecialty: $suggestedSpecialty, suggestions: $suggestions, aiMetadata: $aiMetadata)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$ChatResponseImpl &&
            (identical(other.message, message) || other.message == message) &&
            (identical(other.conversationId, conversationId) ||
                other.conversationId == conversationId) &&
            (identical(other.suggestedSpecialty, suggestedSpecialty) ||
                other.suggestedSpecialty == suggestedSpecialty) &&
            const DeepCollectionEquality()
                .equals(other._suggestions, _suggestions) &&
            const DeepCollectionEquality()
                .equals(other._aiMetadata, _aiMetadata));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
      runtimeType,
      message,
      conversationId,
      suggestedSpecialty,
      const DeepCollectionEquality().hash(_suggestions),
      const DeepCollectionEquality().hash(_aiMetadata));

  /// Create a copy of ChatResponse
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$ChatResponseImplCopyWith<_$ChatResponseImpl> get copyWith =>
      __$$ChatResponseImplCopyWithImpl<_$ChatResponseImpl>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$ChatResponseImplToJson(
      this,
    );
  }
}

abstract class _ChatResponse implements ChatResponse {
  const factory _ChatResponse(
      {required final String message,
      required final String conversationId,
      final String? suggestedSpecialty,
      final List<String>? suggestions,
      final Map<String, dynamic>? aiMetadata}) = _$ChatResponseImpl;

  factory _ChatResponse.fromJson(Map<String, dynamic> json) =
      _$ChatResponseImpl.fromJson;

  @override
  String get message;
  @override
  String get conversationId;
  @override
  String? get suggestedSpecialty;
  @override
  List<String>? get suggestions;
  @override
  Map<String, dynamic>? get aiMetadata;

  /// Create a copy of ChatResponse
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$ChatResponseImplCopyWith<_$ChatResponseImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

AIPreDiagnosis _$AIPreDiagnosisFromJson(Map<String, dynamic> json) {
  return _AIPreDiagnosis.fromJson(json);
}

/// @nodoc
mixin _$AIPreDiagnosis {
  List<String> get possibleCauses => throw _privateConstructorUsedError;
  List<String> get recommendedTests => throw _privateConstructorUsedError;
  String get urgencyLevel =>
      throw _privateConstructorUsedError; // 'low', 'medium', 'high', 'urgent'
  String get suggestedSpecialty => throw _privateConstructorUsedError;
  String? get disclaimer => throw _privateConstructorUsedError;
  double? get confidenceScore => throw _privateConstructorUsedError;

  /// Serializes this AIPreDiagnosis to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of AIPreDiagnosis
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $AIPreDiagnosisCopyWith<AIPreDiagnosis> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $AIPreDiagnosisCopyWith<$Res> {
  factory $AIPreDiagnosisCopyWith(
          AIPreDiagnosis value, $Res Function(AIPreDiagnosis) then) =
      _$AIPreDiagnosisCopyWithImpl<$Res, AIPreDiagnosis>;
  @useResult
  $Res call(
      {List<String> possibleCauses,
      List<String> recommendedTests,
      String urgencyLevel,
      String suggestedSpecialty,
      String? disclaimer,
      double? confidenceScore});
}

/// @nodoc
class _$AIPreDiagnosisCopyWithImpl<$Res, $Val extends AIPreDiagnosis>
    implements $AIPreDiagnosisCopyWith<$Res> {
  _$AIPreDiagnosisCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of AIPreDiagnosis
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? possibleCauses = null,
    Object? recommendedTests = null,
    Object? urgencyLevel = null,
    Object? suggestedSpecialty = null,
    Object? disclaimer = freezed,
    Object? confidenceScore = freezed,
  }) {
    return _then(_value.copyWith(
      possibleCauses: null == possibleCauses
          ? _value.possibleCauses
          : possibleCauses // ignore: cast_nullable_to_non_nullable
              as List<String>,
      recommendedTests: null == recommendedTests
          ? _value.recommendedTests
          : recommendedTests // ignore: cast_nullable_to_non_nullable
              as List<String>,
      urgencyLevel: null == urgencyLevel
          ? _value.urgencyLevel
          : urgencyLevel // ignore: cast_nullable_to_non_nullable
              as String,
      suggestedSpecialty: null == suggestedSpecialty
          ? _value.suggestedSpecialty
          : suggestedSpecialty // ignore: cast_nullable_to_non_nullable
              as String,
      disclaimer: freezed == disclaimer
          ? _value.disclaimer
          : disclaimer // ignore: cast_nullable_to_non_nullable
              as String?,
      confidenceScore: freezed == confidenceScore
          ? _value.confidenceScore
          : confidenceScore // ignore: cast_nullable_to_non_nullable
              as double?,
    ) as $Val);
  }
}

/// @nodoc
abstract class _$$AIPreDiagnosisImplCopyWith<$Res>
    implements $AIPreDiagnosisCopyWith<$Res> {
  factory _$$AIPreDiagnosisImplCopyWith(_$AIPreDiagnosisImpl value,
          $Res Function(_$AIPreDiagnosisImpl) then) =
      __$$AIPreDiagnosisImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call(
      {List<String> possibleCauses,
      List<String> recommendedTests,
      String urgencyLevel,
      String suggestedSpecialty,
      String? disclaimer,
      double? confidenceScore});
}

/// @nodoc
class __$$AIPreDiagnosisImplCopyWithImpl<$Res>
    extends _$AIPreDiagnosisCopyWithImpl<$Res, _$AIPreDiagnosisImpl>
    implements _$$AIPreDiagnosisImplCopyWith<$Res> {
  __$$AIPreDiagnosisImplCopyWithImpl(
      _$AIPreDiagnosisImpl _value, $Res Function(_$AIPreDiagnosisImpl) _then)
      : super(_value, _then);

  /// Create a copy of AIPreDiagnosis
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? possibleCauses = null,
    Object? recommendedTests = null,
    Object? urgencyLevel = null,
    Object? suggestedSpecialty = null,
    Object? disclaimer = freezed,
    Object? confidenceScore = freezed,
  }) {
    return _then(_$AIPreDiagnosisImpl(
      possibleCauses: null == possibleCauses
          ? _value._possibleCauses
          : possibleCauses // ignore: cast_nullable_to_non_nullable
              as List<String>,
      recommendedTests: null == recommendedTests
          ? _value._recommendedTests
          : recommendedTests // ignore: cast_nullable_to_non_nullable
              as List<String>,
      urgencyLevel: null == urgencyLevel
          ? _value.urgencyLevel
          : urgencyLevel // ignore: cast_nullable_to_non_nullable
              as String,
      suggestedSpecialty: null == suggestedSpecialty
          ? _value.suggestedSpecialty
          : suggestedSpecialty // ignore: cast_nullable_to_non_nullable
              as String,
      disclaimer: freezed == disclaimer
          ? _value.disclaimer
          : disclaimer // ignore: cast_nullable_to_non_nullable
              as String?,
      confidenceScore: freezed == confidenceScore
          ? _value.confidenceScore
          : confidenceScore // ignore: cast_nullable_to_non_nullable
              as double?,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$AIPreDiagnosisImpl implements _AIPreDiagnosis {
  const _$AIPreDiagnosisImpl(
      {required final List<String> possibleCauses,
      required final List<String> recommendedTests,
      required this.urgencyLevel,
      required this.suggestedSpecialty,
      this.disclaimer,
      this.confidenceScore})
      : _possibleCauses = possibleCauses,
        _recommendedTests = recommendedTests;

  factory _$AIPreDiagnosisImpl.fromJson(Map<String, dynamic> json) =>
      _$$AIPreDiagnosisImplFromJson(json);

  final List<String> _possibleCauses;
  @override
  List<String> get possibleCauses {
    if (_possibleCauses is EqualUnmodifiableListView) return _possibleCauses;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_possibleCauses);
  }

  final List<String> _recommendedTests;
  @override
  List<String> get recommendedTests {
    if (_recommendedTests is EqualUnmodifiableListView)
      return _recommendedTests;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_recommendedTests);
  }

  @override
  final String urgencyLevel;
// 'low', 'medium', 'high', 'urgent'
  @override
  final String suggestedSpecialty;
  @override
  final String? disclaimer;
  @override
  final double? confidenceScore;

  @override
  String toString() {
    return 'AIPreDiagnosis(possibleCauses: $possibleCauses, recommendedTests: $recommendedTests, urgencyLevel: $urgencyLevel, suggestedSpecialty: $suggestedSpecialty, disclaimer: $disclaimer, confidenceScore: $confidenceScore)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$AIPreDiagnosisImpl &&
            const DeepCollectionEquality()
                .equals(other._possibleCauses, _possibleCauses) &&
            const DeepCollectionEquality()
                .equals(other._recommendedTests, _recommendedTests) &&
            (identical(other.urgencyLevel, urgencyLevel) ||
                other.urgencyLevel == urgencyLevel) &&
            (identical(other.suggestedSpecialty, suggestedSpecialty) ||
                other.suggestedSpecialty == suggestedSpecialty) &&
            (identical(other.disclaimer, disclaimer) ||
                other.disclaimer == disclaimer) &&
            (identical(other.confidenceScore, confidenceScore) ||
                other.confidenceScore == confidenceScore));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
      runtimeType,
      const DeepCollectionEquality().hash(_possibleCauses),
      const DeepCollectionEquality().hash(_recommendedTests),
      urgencyLevel,
      suggestedSpecialty,
      disclaimer,
      confidenceScore);

  /// Create a copy of AIPreDiagnosis
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$AIPreDiagnosisImplCopyWith<_$AIPreDiagnosisImpl> get copyWith =>
      __$$AIPreDiagnosisImplCopyWithImpl<_$AIPreDiagnosisImpl>(
          this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$AIPreDiagnosisImplToJson(
      this,
    );
  }
}

abstract class _AIPreDiagnosis implements AIPreDiagnosis {
  const factory _AIPreDiagnosis(
      {required final List<String> possibleCauses,
      required final List<String> recommendedTests,
      required final String urgencyLevel,
      required final String suggestedSpecialty,
      final String? disclaimer,
      final double? confidenceScore}) = _$AIPreDiagnosisImpl;

  factory _AIPreDiagnosis.fromJson(Map<String, dynamic> json) =
      _$AIPreDiagnosisImpl.fromJson;

  @override
  List<String> get possibleCauses;
  @override
  List<String> get recommendedTests;
  @override
  String get urgencyLevel; // 'low', 'medium', 'high', 'urgent'
  @override
  String get suggestedSpecialty;
  @override
  String? get disclaimer;
  @override
  double? get confidenceScore;

  /// Create a copy of AIPreDiagnosis
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$AIPreDiagnosisImplCopyWith<_$AIPreDiagnosisImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

SymptomAnalysis _$SymptomAnalysisFromJson(Map<String, dynamic> json) {
  return _SymptomAnalysis.fromJson(json);
}

/// @nodoc
mixin _$SymptomAnalysis {
  List<String> get symptoms => throw _privateConstructorUsedError;
  AIPreDiagnosis get preDiagnosis => throw _privateConstructorUsedError;
  String? get patientId => throw _privateConstructorUsedError;
  DateTime? get timestamp => throw _privateConstructorUsedError;

  /// Serializes this SymptomAnalysis to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of SymptomAnalysis
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $SymptomAnalysisCopyWith<SymptomAnalysis> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $SymptomAnalysisCopyWith<$Res> {
  factory $SymptomAnalysisCopyWith(
          SymptomAnalysis value, $Res Function(SymptomAnalysis) then) =
      _$SymptomAnalysisCopyWithImpl<$Res, SymptomAnalysis>;
  @useResult
  $Res call(
      {List<String> symptoms,
      AIPreDiagnosis preDiagnosis,
      String? patientId,
      DateTime? timestamp});

  $AIPreDiagnosisCopyWith<$Res> get preDiagnosis;
}

/// @nodoc
class _$SymptomAnalysisCopyWithImpl<$Res, $Val extends SymptomAnalysis>
    implements $SymptomAnalysisCopyWith<$Res> {
  _$SymptomAnalysisCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of SymptomAnalysis
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? symptoms = null,
    Object? preDiagnosis = null,
    Object? patientId = freezed,
    Object? timestamp = freezed,
  }) {
    return _then(_value.copyWith(
      symptoms: null == symptoms
          ? _value.symptoms
          : symptoms // ignore: cast_nullable_to_non_nullable
              as List<String>,
      preDiagnosis: null == preDiagnosis
          ? _value.preDiagnosis
          : preDiagnosis // ignore: cast_nullable_to_non_nullable
              as AIPreDiagnosis,
      patientId: freezed == patientId
          ? _value.patientId
          : patientId // ignore: cast_nullable_to_non_nullable
              as String?,
      timestamp: freezed == timestamp
          ? _value.timestamp
          : timestamp // ignore: cast_nullable_to_non_nullable
              as DateTime?,
    ) as $Val);
  }

  /// Create a copy of SymptomAnalysis
  /// with the given fields replaced by the non-null parameter values.
  @override
  @pragma('vm:prefer-inline')
  $AIPreDiagnosisCopyWith<$Res> get preDiagnosis {
    return $AIPreDiagnosisCopyWith<$Res>(_value.preDiagnosis, (value) {
      return _then(_value.copyWith(preDiagnosis: value) as $Val);
    });
  }
}

/// @nodoc
abstract class _$$SymptomAnalysisImplCopyWith<$Res>
    implements $SymptomAnalysisCopyWith<$Res> {
  factory _$$SymptomAnalysisImplCopyWith(_$SymptomAnalysisImpl value,
          $Res Function(_$SymptomAnalysisImpl) then) =
      __$$SymptomAnalysisImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call(
      {List<String> symptoms,
      AIPreDiagnosis preDiagnosis,
      String? patientId,
      DateTime? timestamp});

  @override
  $AIPreDiagnosisCopyWith<$Res> get preDiagnosis;
}

/// @nodoc
class __$$SymptomAnalysisImplCopyWithImpl<$Res>
    extends _$SymptomAnalysisCopyWithImpl<$Res, _$SymptomAnalysisImpl>
    implements _$$SymptomAnalysisImplCopyWith<$Res> {
  __$$SymptomAnalysisImplCopyWithImpl(
      _$SymptomAnalysisImpl _value, $Res Function(_$SymptomAnalysisImpl) _then)
      : super(_value, _then);

  /// Create a copy of SymptomAnalysis
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? symptoms = null,
    Object? preDiagnosis = null,
    Object? patientId = freezed,
    Object? timestamp = freezed,
  }) {
    return _then(_$SymptomAnalysisImpl(
      symptoms: null == symptoms
          ? _value._symptoms
          : symptoms // ignore: cast_nullable_to_non_nullable
              as List<String>,
      preDiagnosis: null == preDiagnosis
          ? _value.preDiagnosis
          : preDiagnosis // ignore: cast_nullable_to_non_nullable
              as AIPreDiagnosis,
      patientId: freezed == patientId
          ? _value.patientId
          : patientId // ignore: cast_nullable_to_non_nullable
              as String?,
      timestamp: freezed == timestamp
          ? _value.timestamp
          : timestamp // ignore: cast_nullable_to_non_nullable
              as DateTime?,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$SymptomAnalysisImpl implements _SymptomAnalysis {
  const _$SymptomAnalysisImpl(
      {required final List<String> symptoms,
      required this.preDiagnosis,
      this.patientId,
      this.timestamp})
      : _symptoms = symptoms;

  factory _$SymptomAnalysisImpl.fromJson(Map<String, dynamic> json) =>
      _$$SymptomAnalysisImplFromJson(json);

  final List<String> _symptoms;
  @override
  List<String> get symptoms {
    if (_symptoms is EqualUnmodifiableListView) return _symptoms;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_symptoms);
  }

  @override
  final AIPreDiagnosis preDiagnosis;
  @override
  final String? patientId;
  @override
  final DateTime? timestamp;

  @override
  String toString() {
    return 'SymptomAnalysis(symptoms: $symptoms, preDiagnosis: $preDiagnosis, patientId: $patientId, timestamp: $timestamp)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$SymptomAnalysisImpl &&
            const DeepCollectionEquality().equals(other._symptoms, _symptoms) &&
            (identical(other.preDiagnosis, preDiagnosis) ||
                other.preDiagnosis == preDiagnosis) &&
            (identical(other.patientId, patientId) ||
                other.patientId == patientId) &&
            (identical(other.timestamp, timestamp) ||
                other.timestamp == timestamp));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
      runtimeType,
      const DeepCollectionEquality().hash(_symptoms),
      preDiagnosis,
      patientId,
      timestamp);

  /// Create a copy of SymptomAnalysis
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$SymptomAnalysisImplCopyWith<_$SymptomAnalysisImpl> get copyWith =>
      __$$SymptomAnalysisImplCopyWithImpl<_$SymptomAnalysisImpl>(
          this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$SymptomAnalysisImplToJson(
      this,
    );
  }
}

abstract class _SymptomAnalysis implements SymptomAnalysis {
  const factory _SymptomAnalysis(
      {required final List<String> symptoms,
      required final AIPreDiagnosis preDiagnosis,
      final String? patientId,
      final DateTime? timestamp}) = _$SymptomAnalysisImpl;

  factory _SymptomAnalysis.fromJson(Map<String, dynamic> json) =
      _$SymptomAnalysisImpl.fromJson;

  @override
  List<String> get symptoms;
  @override
  AIPreDiagnosis get preDiagnosis;
  @override
  String? get patientId;
  @override
  DateTime? get timestamp;

  /// Create a copy of SymptomAnalysis
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$SymptomAnalysisImplCopyWith<_$SymptomAnalysisImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
