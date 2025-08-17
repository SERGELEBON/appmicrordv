import 'package:freezed_annotation/freezed_annotation.dart';

part 'chat.freezed.dart';
part 'chat.g.dart';

@freezed
class ChatMessage with _$ChatMessage {
  const factory ChatMessage({
    required String id,
    required String content,
    required String role, // 'user' or 'assistant'
    required DateTime timestamp,
    String? type, // 'text', 'suggestion', 'diagnosis'
    Map<String, dynamic>? metadata,
  }) = _ChatMessage;

  factory ChatMessage.fromJson(Map<String, dynamic> json) => 
      _$ChatMessageFromJson(json);
}

@freezed
class ChatRequest with _$ChatRequest {
  const factory ChatRequest({
    required String message,
    String? conversationId,
    String? context,
  }) = _ChatRequest;

  factory ChatRequest.fromJson(Map<String, dynamic> json) => 
      _$ChatRequestFromJson(json);
}

@freezed
class ChatResponse with _$ChatResponse {
  const factory ChatResponse({
    required String message,
    required String conversationId,
    String? suggestedSpecialty,
    List<String>? suggestions,
    Map<String, dynamic>? aiMetadata,
  }) = _ChatResponse;

  factory ChatResponse.fromJson(Map<String, dynamic> json) => 
      _$ChatResponseFromJson(json);
}

@freezed
class AIPreDiagnosis with _$AIPreDiagnosis {
  const factory AIPreDiagnosis({
    required List<String> possibleCauses,
    required List<String> recommendedTests,
    required String urgencyLevel, // 'low', 'medium', 'high', 'urgent'
    required String suggestedSpecialty,
    String? disclaimer,
    double? confidenceScore,
  }) = _AIPreDiagnosis;

  factory AIPreDiagnosis.fromJson(Map<String, dynamic> json) => 
      _$AIPreDiagnosisFromJson(json);
}

@freezed
class SymptomAnalysis with _$SymptomAnalysis {
  const factory SymptomAnalysis({
    required List<String> symptoms,
    required AIPreDiagnosis preDiagnosis,
    String? patientId,
    DateTime? timestamp,
  }) = _SymptomAnalysis;

  factory SymptomAnalysis.fromJson(Map<String, dynamic> json) => 
      _$SymptomAnalysisFromJson(json);
}