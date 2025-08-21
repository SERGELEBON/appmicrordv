import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:speech_to_text/speech_to_text.dart';
import 'package:flutter_tts/flutter_tts.dart';
import '../models/chat.dart';
import '../services/chat_service.dart';
import '../services/mock_service.dart';
import '../constants/app_constants.dart';
import 'auth_provider.dart';

final chatServiceProvider = Provider<ChatService>((ref) {
  final apiService = ref.read(apiServiceProvider);
  return ChatService(apiService.dio);
});

final chatProvider = StateNotifierProvider<ChatNotifier, ChatState>((ref) {
  final chatService = ref.read(chatServiceProvider);
  return ChatNotifier(chatService);
});

final aiDiagnosisProvider = StateNotifierProvider<AIDiagnosisNotifier, AIDiagnosisState>((ref) {
  final chatService = ref.read(chatServiceProvider);
  return AIDiagnosisNotifier(chatService);
});

final speechProvider = StateNotifierProvider<SpeechNotifier, SpeechState>((ref) {
  return SpeechNotifier();
});

class ChatState {
  final List<ChatMessage> messages;
  final String? currentConversationId;
  final bool isLoading;
  final String? error;
  final bool isListening;

  const ChatState({
    this.messages = const [],
    this.currentConversationId,
    this.isLoading = false,
    this.error,
    this.isListening = false,
  });

  ChatState copyWith({
    List<ChatMessage>? messages,
    String? currentConversationId,
    bool? isLoading,
    String? error,
    bool? isListening,
  }) {
    return ChatState(
      messages: messages ?? this.messages,
      currentConversationId: currentConversationId ?? this.currentConversationId,
      isLoading: isLoading ?? this.isLoading,
      error: error,
      isListening: isListening ?? this.isListening,
    );
  }
}

class AIDiagnosisState {
  final AIPreDiagnosis? preDiagnosis;
  final SymptomAnalysis? symptomAnalysis;
  final List<String> currentSymptoms;
  final bool isLoading;
  final String? error;

  const AIDiagnosisState({
    this.preDiagnosis,
    this.symptomAnalysis,
    this.currentSymptoms = const [],
    this.isLoading = false,
    this.error,
  });

  AIDiagnosisState copyWith({
    AIPreDiagnosis? preDiagnosis,
    SymptomAnalysis? symptomAnalysis,
    List<String>? currentSymptoms,
    bool? isLoading,
    String? error,
  }) {
    return AIDiagnosisState(
      preDiagnosis: preDiagnosis ?? this.preDiagnosis,
      symptomAnalysis: symptomAnalysis ?? this.symptomAnalysis,
      currentSymptoms: currentSymptoms ?? this.currentSymptoms,
      isLoading: isLoading ?? this.isLoading,
      error: error,
    );
  }
}

class SpeechState {
  final bool isListening;
  final bool isInitialized;
  final bool isLoading;
  final String recognizedText;
  final bool isSpeaking;
  final String? error;

  const SpeechState({
    this.isListening = false,
    this.isInitialized = false,
    this.isLoading = false,
    this.recognizedText = '',
    this.isSpeaking = false,
    this.error,
  });

  SpeechState copyWith({
    bool? isListening,
    bool? isInitialized,
    bool? isLoading,
    String? recognizedText,
    bool? isSpeaking,
    String? error,
  }) {
    return SpeechState(
      isListening: isListening ?? this.isListening,
      isInitialized: isInitialized ?? this.isInitialized,
      isLoading: isLoading ?? this.isLoading,
      recognizedText: recognizedText ?? this.recognizedText,
      isSpeaking: isSpeaking ?? this.isSpeaking,
      error: error,
    );
  }
}

class ChatNotifier extends StateNotifier<ChatState> {
  final ChatService _chatService;

  ChatNotifier(this._chatService) : super(const ChatState());

  Future<void> sendMessage(String message, {String? context}) async {
    final userMessage = ChatMessage(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      content: message,
      role: 'user',
      timestamp: DateTime.now(),
    );

    state = state.copyWith(
      messages: [...state.messages, userMessage],
      isLoading: true,
      error: null,
    );

    try {
      Map<String, dynamic> responseData;
      
      if (AppConstants.useMockData) {
        responseData = await MockService.mockChatMessage(message);
        
        final aiMessage = ChatMessage(
          id: DateTime.now().millisecondsSinceEpoch.toString(),
          content: responseData['message'],
          role: 'assistant',
          timestamp: DateTime.now(),
          metadata: responseData['aiMetadata'],
        );

        state = state.copyWith(
          messages: [...state.messages, aiMessage],
          currentConversationId: responseData['conversationId'],
          isLoading: false,
        );

        // Si l'IA suggère une spécialité, on peut déclencher une action
        if (responseData['suggestedSpecialty'] != null) {
          _handleSpecialtySuggestion(responseData['suggestedSpecialty']!);
        }
      } else {
        // Utiliser l'API réelle
        final request = ChatRequest(
          message: message,
          conversationId: state.currentConversationId,
          context: context,
        );

        final response = await _chatService.sendMessage(request);

        final aiMessage = ChatMessage(
          id: DateTime.now().millisecondsSinceEpoch.toString(),
          content: response.message,
          role: 'assistant',
          timestamp: DateTime.now(),
          metadata: response.aiMetadata,
        );

        state = state.copyWith(
          messages: [...state.messages, aiMessage],
          currentConversationId: response.conversationId,
          isLoading: false,
        );

        if (response.suggestedSpecialty != null) {
          _handleSpecialtySuggestion(response.suggestedSpecialty!);
        }
      }
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: 'Erreur lors de l\'envoi du message',
      );
    }
  }

  void _handleSpecialtySuggestion(String specialty) {
    final suggestionMessage = ChatMessage(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      content: 'Je vous recommande de consulter un $specialty. Voulez-vous que je vous aide à prendre rendez-vous ?',
      role: 'assistant',
      timestamp: DateTime.now(),
      type: 'suggestion',
      metadata: {'suggestedSpecialty': specialty},
    );

    state = state.copyWith(
      messages: [...state.messages, suggestionMessage],
    );
  }

  Future<void> loadConversation(String conversationId) async {
    state = state.copyWith(isLoading: true, error: null);

    try {
      final messages = await _chatService.getConversation(conversationId);
      state = state.copyWith(
        messages: messages,
        currentConversationId: conversationId,
        isLoading: false,
      );
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: 'Erreur lors du chargement de la conversation',
      );
    }
  }

  void startNewConversation() {
    state = const ChatState();
  }

  void clearError() {
    state = state.copyWith(error: null);
  }
}

class AIDiagnosisNotifier extends StateNotifier<AIDiagnosisState> {
  final ChatService _chatService;

  AIDiagnosisNotifier(this._chatService) : super(const AIDiagnosisState());

  void addSymptom(String symptom) {
    if (!state.currentSymptoms.contains(symptom)) {
      state = state.copyWith(
        currentSymptoms: [...state.currentSymptoms, symptom],
      );
    }
  }

  void removeSymptom(String symptom) {
    state = state.copyWith(
      currentSymptoms: state.currentSymptoms.where((s) => s != symptom).toList(),
    );
  }

  Future<void> analyzeSymptoms() async {
    if (state.currentSymptoms.isEmpty) return;

    state = state.copyWith(isLoading: true, error: null);

    try {
      final symptomsData = {
        'symptoms': state.currentSymptoms,
        'timestamp': DateTime.now().toIso8601String(),
      };

      final preDiagnosis = await _chatService.analyzeSymptoms(symptomsData);

      state = state.copyWith(
        preDiagnosis: preDiagnosis,
        isLoading: false,
      );
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: 'Erreur lors de l\'analyse des symptômes',
      );
    }
  }

  Future<void> getDetailedPreDiagnosis(Map<String, dynamic> questionnaire) async {
    state = state.copyWith(isLoading: true, error: null);

    try {
      final analysis = await _chatService.getPreDiagnosis(questionnaire);
      state = state.copyWith(
        symptomAnalysis: analysis,
        isLoading: false,
      );
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: 'Erreur lors du pré-diagnostic détaillé',
      );
    }
  }

  void clearSymptoms() {
    state = state.copyWith(
      currentSymptoms: [],
      preDiagnosis: null,
      symptomAnalysis: null,
    );
  }

  void clearError() {
    state = state.copyWith(error: null);
  }
}

class SpeechNotifier extends StateNotifier<SpeechState> {
  final SpeechToText _speechToText = SpeechToText();
  FlutterTts? _flutterTts; // Lazy initialization
  bool _isInitializing = false;

  SpeechNotifier() : super(const SpeechState()) {
    // Optimisation : Initialisation asynchrone en arrière-plan
    _initializeSpeechAsync();
  }

  // Initialisation non-bloquante en arrière-plan
  void _initializeSpeechAsync() {
    Future.microtask(() async {
      try {
        await _initializeSpeech();
      } catch (e) {
        // Échec silencieux - l'utilisateur peut réessayer manuellement
        state = state.copyWith(
          error: 'Reconnaissance vocale non disponible',
          isLoading: false,
        );
      }
    });
  }

  Future<void> _initializeSpeech() async {
    if (state.isInitialized || _isInitializing) return;
    
    _isInitializing = true;
    
    try {
      // Initialisation parallèle pour optimiser les performances
      final List<Future> initFutures = [
        _speechToText.initialize(),
        _initializeTts(),
      ];
      
      final results = await Future.wait(initFutures);
      final speechAvailable = results[0] as bool;
      
      state = state.copyWith(
        isInitialized: speechAvailable,
        isLoading: false,
      );
    } catch (e) {
      state = state.copyWith(
        error: 'Erreur d\'initialisation de la reconnaissance vocale',
        isLoading: false,
      );
    } finally {
      _isInitializing = false;
    }
  }

  Future<void> _initializeTts() async {
    try {
      _flutterTts ??= FlutterTts();
      await _flutterTts!.setLanguage('fr-FR');
      await _flutterTts!.setSpeechRate(0.5);
      // Configuration optimisée pour réduire la latence
      await _flutterTts!.setPitch(1.0);
      await _flutterTts!.setVolume(0.8);
    } catch (e) {
      // TTS non critique - continuer sans
      _flutterTts = null;
    }
  }

  Future<void> startListening() async {
    // Vérification rapide - ne pas bloquer l'UI
    if (!state.isInitialized && !_isInitializing) {
      state = state.copyWith(
        error: 'Reconnaissance vocale en cours d\'initialisation...',
      );
      return;
    }
    
    if (!state.isInitialized) return;

    state = state.copyWith(isListening: true, recognizedText: '', error: null);

    try {
      await _speechToText.listen(
        onResult: (result) {
          state = state.copyWith(recognizedText: result.recognizedWords);
        },
        localeId: 'fr_FR',
        listenFor: const Duration(seconds: 30), // Limite de temps
        pauseFor: const Duration(seconds: 3),   // Pause automatique
      );
    } catch (e) {
      state = state.copyWith(
        isListening: false,
        error: 'Erreur lors de l\'écoute',
      );
    }
  }

  Future<void> stopListening() async {
    try {
      await _speechToText.stop();
    } catch (e) {
      // Ignorer les erreurs de stop
    } finally {
      state = state.copyWith(isListening: false);
    }
  }

  Future<void> speak(String text) async {
    if (_flutterTts == null) {
      state = state.copyWith(
        error: 'Synthèse vocale non disponible',
      );
      return;
    }
    
    state = state.copyWith(isSpeaking: true);
    
    try {
      // Optimisation : Limiter la longueur du texte pour éviter les délais
      final truncatedText = text.length > 200 ? '${text.substring(0, 200)}...' : text;
      await _flutterTts!.speak(truncatedText);
    } catch (e) {
      state = state.copyWith(
        error: 'Erreur lors de la synthèse vocale',
      );
    } finally {
      state = state.copyWith(isSpeaking: false);
    }
  }

  Future<void> stopSpeaking() async {
    if (_flutterTts != null) {
      await _flutterTts!.stop();
    }
    state = state.copyWith(isSpeaking: false);
  }

  void clearError() {
    state = state.copyWith(error: null);
  }
}