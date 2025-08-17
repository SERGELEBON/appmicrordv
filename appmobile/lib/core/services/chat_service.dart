import 'package:retrofit/retrofit.dart';
import 'package:dio/dio.dart';
import '../models/chat.dart';
import '../constants/app_constants.dart';

part 'chat_service.g.dart';

@RestApi(baseUrl: AppConstants.baseUrl)
abstract class ChatService {
  factory ChatService(Dio dio, {String baseUrl}) = _ChatService;

  @POST('/chat/chat')
  Future<ChatResponse> sendMessage(@Body() ChatRequest request);

  @GET('/chat/conversations/{id}')
  Future<List<ChatMessage>> getConversation(@Path('id') String conversationId);

  @GET('/chat/conversations')
  Future<List<Map<String, dynamic>>> getConversations();

  @DELETE('/chat/conversations/{id}')
  Future<void> deleteConversation(@Path('id') String conversationId);

  @POST('/chat/analyze-symptoms')
  Future<AIPreDiagnosis> analyzeSymptoms(@Body() Map<String, dynamic> symptoms);

  @POST('/chat/pre-diagnosis')
  Future<SymptomAnalysis> getPreDiagnosis(@Body() Map<String, dynamic> questionnaire);

  @POST('/chat/consultation-summary')
  Future<Map<String, dynamic>> generateConsultationSummary(
    @Body() Map<String, dynamic> consultationData
  );

  @GET('/chat/post-consultation-care/{appointmentId}')
  Future<List<String>> getPostConsultationCare(
    @Path('appointmentId') int appointmentId
  );

  @POST('/chat/prioritize-appointments')
  Future<List<Map<String, dynamic>>> prioritizeAppointments(
    @Body() List<Map<String, dynamic>> appointments
  );

  @GET('/chat/ai-suggestions/{patientId}')
  Future<List<String>> getAISuggestions(@Path('patientId') int patientId);

  @POST('/chat/voice-to-text')
  Future<Map<String, String>> convertVoiceToText(@Body() Map<String, dynamic> audioData);

  @POST('/chat/text-to-voice')
  Future<Map<String, dynamic>> convertTextToVoice(@Body() Map<String, String> textData);
}