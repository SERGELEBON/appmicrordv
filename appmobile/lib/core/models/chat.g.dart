// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'chat.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$ChatMessageImpl _$$ChatMessageImplFromJson(Map<String, dynamic> json) =>
    _$ChatMessageImpl(
      id: json['id'] as String,
      content: json['content'] as String,
      role: json['role'] as String,
      timestamp: DateTime.parse(json['timestamp'] as String),
      type: json['type'] as String?,
      metadata: json['metadata'] as Map<String, dynamic>?,
    );

Map<String, dynamic> _$$ChatMessageImplToJson(_$ChatMessageImpl instance) =>
    <String, dynamic>{
      'id': instance.id,
      'content': instance.content,
      'role': instance.role,
      'timestamp': instance.timestamp.toIso8601String(),
      'type': instance.type,
      'metadata': instance.metadata,
    };

_$ChatRequestImpl _$$ChatRequestImplFromJson(Map<String, dynamic> json) =>
    _$ChatRequestImpl(
      message: json['message'] as String,
      conversationId: json['conversationId'] as String?,
      context: json['context'] as String?,
    );

Map<String, dynamic> _$$ChatRequestImplToJson(_$ChatRequestImpl instance) =>
    <String, dynamic>{
      'message': instance.message,
      'conversationId': instance.conversationId,
      'context': instance.context,
    };

_$ChatResponseImpl _$$ChatResponseImplFromJson(Map<String, dynamic> json) =>
    _$ChatResponseImpl(
      message: json['message'] as String,
      conversationId: json['conversationId'] as String,
      suggestedSpecialty: json['suggestedSpecialty'] as String?,
      suggestions: (json['suggestions'] as List<dynamic>?)
          ?.map((e) => e as String)
          .toList(),
      aiMetadata: json['aiMetadata'] as Map<String, dynamic>?,
    );

Map<String, dynamic> _$$ChatResponseImplToJson(_$ChatResponseImpl instance) =>
    <String, dynamic>{
      'message': instance.message,
      'conversationId': instance.conversationId,
      'suggestedSpecialty': instance.suggestedSpecialty,
      'suggestions': instance.suggestions,
      'aiMetadata': instance.aiMetadata,
    };

_$AIPreDiagnosisImpl _$$AIPreDiagnosisImplFromJson(Map<String, dynamic> json) =>
    _$AIPreDiagnosisImpl(
      possibleCauses: (json['possibleCauses'] as List<dynamic>)
          .map((e) => e as String)
          .toList(),
      recommendedTests: (json['recommendedTests'] as List<dynamic>)
          .map((e) => e as String)
          .toList(),
      urgencyLevel: json['urgencyLevel'] as String,
      suggestedSpecialty: json['suggestedSpecialty'] as String,
      disclaimer: json['disclaimer'] as String?,
      confidenceScore: (json['confidenceScore'] as num?)?.toDouble(),
    );

Map<String, dynamic> _$$AIPreDiagnosisImplToJson(
        _$AIPreDiagnosisImpl instance) =>
    <String, dynamic>{
      'possibleCauses': instance.possibleCauses,
      'recommendedTests': instance.recommendedTests,
      'urgencyLevel': instance.urgencyLevel,
      'suggestedSpecialty': instance.suggestedSpecialty,
      'disclaimer': instance.disclaimer,
      'confidenceScore': instance.confidenceScore,
    };

_$SymptomAnalysisImpl _$$SymptomAnalysisImplFromJson(
        Map<String, dynamic> json) =>
    _$SymptomAnalysisImpl(
      symptoms:
          (json['symptoms'] as List<dynamic>).map((e) => e as String).toList(),
      preDiagnosis:
          AIPreDiagnosis.fromJson(json['preDiagnosis'] as Map<String, dynamic>),
      patientId: json['patientId'] as String?,
      timestamp: json['timestamp'] == null
          ? null
          : DateTime.parse(json['timestamp'] as String),
    );

Map<String, dynamic> _$$SymptomAnalysisImplToJson(
        _$SymptomAnalysisImpl instance) =>
    <String, dynamic>{
      'symptoms': instance.symptoms,
      'preDiagnosis': instance.preDiagnosis,
      'patientId': instance.patientId,
      'timestamp': instance.timestamp?.toIso8601String(),
    };
