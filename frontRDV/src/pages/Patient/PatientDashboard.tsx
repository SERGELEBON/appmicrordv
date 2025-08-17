import React, { useState, useEffect } from 'react';
import { 
  Calendar, 
  Clock, 
  User, 
  FileText, 
  Heart,
  MessageSquare,
  CheckCircle,
  XCircle,
  AlertCircle,
  Phone,
  Video,
  Activity,
  TrendingUp,
  Bell,
  Search,
  Filter,
  MoreVertical,
  Plus,
  Stethoscope,
  Pill,
  UserCheck,
  Bot,
  Brain,
  Clipboard,
  MapPin,
  Star,
  Shield,
  Eye
} from 'lucide-react';
import { rdvService, type Medecin, type RendezVous as RdvType } from '../../services/rdvService';
import { useAuth } from '../../context/AuthContext';

interface Doctor {
  id: number;
  name: string;
  specialty: string;
  rating: number;
  reviews: number;
  availability: string[];
  location: string;
  photo?: string;
  price: number;
}

interface Appointment {
  id: number;
  doctorName: string;
  doctorSpecialty: string;
  date: string;
  time: string;
  type: 'consultation' | 'follow-up' | 'emergency';
  status: 'scheduled' | 'completed' | 'cancelled';
  reason: string;
  location: string;
  canReschedule: boolean;
}

interface HealthRecord {
  id: number;
  date: string;
  type: 'consultation' | 'prescription' | 'test-result';
  title: string;
  doctor: string;
  content: string;
}

interface ChatMessage {
  id: number;
  text: string;
  isBot: boolean;
  timestamp: string;
  suggestions?: string[];
}

const PatientDashboard = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState<'overview' | 'appointments' | 'doctors' | 'health' | 'ai-chat' | 'follow-up'>('overview');
  const [searchTerm, setSearchTerm] = useState('');
  const [chatInput, setChatInput] = useState('');
  const [availableDoctors, setAvailableDoctors] = useState<Medecin[]>([]);
  const [myAppointments, setMyAppointments] = useState<RdvType[]>([]);
  const [loading, setLoading] = useState(true);
  const [chatMessages, setChatMessages] = useState<ChatMessage[]>([
    {
      id: 1,
      text: "Bonjour ! Je suis votre assistant médical IA. Comment puis-je vous aider aujourd'hui ?",
      isBot: true,
      timestamp: '10:00',
      suggestions: ['J\'ai mal à la gorge', 'Symptômes de grippe', 'Douleur abdominale', 'Prendre un rendez-vous']
    }
  ]);

  // Charger les données depuis l'API
  useEffect(() => {
    const loadData = async () => {
      if (!user?.id) return;
      
      try {
        setLoading(true);
        const [doctors, appointments] = await Promise.all([
          rdvService.getAllMedecins(),
          rdvService.getRendezVousByPatient(user.id)
        ]);
        
        setAvailableDoctors(doctors);
        setMyAppointments(appointments);
      } catch (error) {
        console.error('Erreur lors du chargement des données:', error);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [user?.id]);

  const healthRecords: HealthRecord[] = [
    {
      id: 1,
      date: '2025-08-10',
      type: 'consultation',
      title: 'Consultation générale',
      doctor: 'Dr. Marie Dupont',
      content: 'Bilan de santé général. Tension normale, poids stable. Prescription vitamines.'
    },
    {
      id: 2,
      date: '2025-08-05',
      type: 'prescription',
      title: 'Ordonnance',
      doctor: 'Dr. Pierre Martin',
      content: 'Antibiotiques pour sinusite - Amoxicilline 1g, 3 fois par jour, 7 jours'
    },
    {
      id: 3,
      date: '2025-08-01',
      type: 'test-result',
      title: 'Résultats analyses sanguines',
      doctor: 'Laboratoire Pasteur',
      content: 'Glycémie: normale, Cholestérol: légèrement élevé, Créatinine: normale'
    }
  ];

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  };

  const formatTime = (dateTimeString: string) => {
    return new Date(dateTimeString).toLocaleTimeString('fr-FR', {
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getStatusBadge = (status: string) => {
    const statusConfig = {
      planifie: 'bg-blue-100 text-blue-800',
      confirme: 'bg-green-100 text-green-800', 
      en_cours: 'bg-yellow-100 text-yellow-800',
      termine: 'bg-green-100 text-green-800',
      annule: 'bg-red-100 text-red-800',
      absent: 'bg-gray-100 text-gray-800',
      // Compatibilité avec anciens statuts
      scheduled: 'bg-blue-100 text-blue-800',
      completed: 'bg-green-100 text-green-800',
      cancelled: 'bg-red-100 text-red-800'
    };
    
    return statusConfig[status.toLowerCase() as keyof typeof statusConfig] || 'bg-gray-100 text-gray-800';
  };

  const getRecordTypeIcon = (type: string) => {
    switch (type) {
      case 'consultation': return <Stethoscope className="w-4 h-4 text-blue-600" />;
      case 'prescription': return <Pill className="w-4 h-4 text-green-600" />;
      case 'test-result': return <FileText className="w-4 h-4 text-purple-600" />;
      default: return <FileText className="w-4 h-4 text-gray-600" />;
    }
  };

  const handleSendMessage = () => {
    if (!chatInput.trim()) return;

    const userMessage: ChatMessage = {
      id: chatMessages.length + 1,
      text: chatInput,
      isBot: false,
      timestamp: new Date().toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })
    };

    setChatMessages([...chatMessages, userMessage]);

    // Simulate AI response
    setTimeout(() => {
      let aiResponse = '';
      let suggestions: string[] = [];

      if (chatInput.toLowerCase().includes('mal à la gorge') || chatInput.toLowerCase().includes('gorge')) {
        aiResponse = 'D\'après vos symptômes de mal de gorge, je vous recommande de consulter un ORL. Cela pourrait être une angine ou une inflammation. Voulez-vous que je vous trouve un ORL disponible rapidement ?';
        suggestions = ['Trouver un ORL', 'Symptômes d\'angine', 'Remèdes naturels'];
      } else if (chatInput.toLowerCase().includes('grippe') || chatInput.toLowerCase().includes('fièvre')) {
        aiResponse = 'Les symptômes de grippe nécessitent un suivi médical. Je recommande une consultation en médecine générale dans les 24h. Voulez-vous prendre rendez-vous ?';
        suggestions = ['Prendre RDV médecin généraliste', 'Conseils grippe', 'Urgences si aggravation'];
      } else if (chatInput.toLowerCase().includes('abdominale') || chatInput.toLowerCase().includes('ventre')) {
        aiResponse = 'Les douleurs abdominales peuvent avoir diverses causes. Il est important de consulter rapidement. Selon l\'intensité, cela peut nécessiter une consultation d\'urgence ou un rendez-vous en médecine générale.';
        suggestions = ['Consultation d\'urgence', 'RDV médecin généraliste', 'Conseils alimentation'];
      } else {
        aiResponse = 'Je comprends votre préoccupation. Pouvez-vous me décrire plus précisément vos symptômes ? Cela m\'aidera à vous orienter vers le bon spécialiste.';
        suggestions = ['Décrire mes symptômes', 'Types de médecins', 'Prendre RDV'];
      }

      const botMessage: ChatMessage = {
        id: chatMessages.length + 2,
        text: aiResponse,
        isBot: true,
        timestamp: new Date().toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' }),
        suggestions
      };

      setChatMessages(prev => [...prev, botMessage]);
    }, 1500);

    setChatInput('');
  };

  const handleSuggestionClick = (suggestion: string) => {
    setChatInput(suggestion);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Mon Espace Santé</h1>
          <p className="text-gray-600 mt-2">Jean Martin - Gestion de vos rendez-vous et suivi médical</p>
        </div>

        {/* Navigation Tabs */}
        <div className="border-b border-gray-200 mb-8">
          <nav className="-mb-px flex space-x-8">
            {[
              { id: 'overview', label: 'Vue d\'ensemble', icon: Activity },
              { id: 'appointments', label: 'Mes RDV', icon: Calendar },
              { id: 'doctors', label: 'Trouver un médecin', icon: UserCheck },
              { id: 'health', label: 'Dossier médical', icon: FileText },
              { id: 'ai-chat', label: 'Assistant IA', icon: Bot },
              { id: 'follow-up', label: 'Suivi personnalisé', icon: Heart }
            ].map((tab) => {
              const Icon = tab.icon;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id as any)}
                  className={`flex items-center py-2 px-1 border-b-2 font-medium text-sm ${
                    activeTab === tab.id
                      ? 'border-blue-500 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
                >
                  <Icon className="w-5 h-5 mr-2" />
                  {tab.label}
                </button>
              );
            })}
          </nav>
        </div>

        {/* Content */}
        {activeTab === 'overview' && (
          <div className="space-y-8">
            {/* Quick Stats */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
              {[
                { title: 'Prochains RDV', value: myAppointments.filter(apt => apt.statut === 'PLANIFIE' || apt.statut === 'CONFIRME').length, icon: Calendar, color: 'blue' },
                { title: 'Consultations ce mois', value: myAppointments.filter(apt => apt.statut === 'TERMINE').length, icon: Stethoscope, color: 'green' },
                { title: 'Rappels santé', value: myAppointments.filter(apt => apt.rappelEnvoye).length, icon: Bell, color: 'orange' },
                { title: 'Total RDV', value: myAppointments.length, icon: Heart, color: 'red' }
              ].map((stat, index) => {
                const Icon = stat.icon;
                return (
                  <div key={index} className="bg-white rounded-xl shadow-sm p-6">
                    <div className="flex items-center">
                      <div className={`p-2 rounded-lg bg-${stat.color}-100`}>
                        <Icon className={`w-6 h-6 text-${stat.color}-600`} />
                      </div>
                      <div className="ml-4">
                        <p className="text-sm font-medium text-gray-600">{stat.title}</p>
                        <p className="text-2xl font-bold text-gray-900">{stat.value}</p>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>

            {/* Prochains rendez-vous */}
            <div className="bg-white rounded-xl shadow-sm">
              <div className="p-6 border-b border-gray-200">
                <h3 className="text-lg font-semibold text-gray-900">Prochains rendez-vous</h3>
              </div>
              <div className="p-6">
                <div className="space-y-4">
                  {loading ? (
                    <div className="flex justify-center py-4">
                      <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
                    </div>
                  ) : myAppointments.filter(apt => apt.statut === 'PLANIFIE' || apt.statut === 'CONFIRME').map((appointment) => (
                    <div key={appointment.id} className="flex items-center justify-between p-4 border border-gray-200 rounded-lg hover:bg-gray-50">
                      <div className="flex items-center space-x-4">
                        <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                          <Stethoscope className="w-6 h-6 text-blue-600" />
                        </div>
                        <div>
                          <p className="font-medium text-gray-900">Dr. {appointment.medecinPrenom} {appointment.medecinNom}</p>
                          <p className="text-sm text-gray-600">{appointment.medecinSpecialite}</p>
                          <p className="text-sm text-gray-500">{formatDate(appointment.dateHeureDebut)} à {formatTime(appointment.dateHeureDebut)}</p>
                        </div>
                      </div>
                      <div className="flex items-center space-x-4">
                        <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusBadge(appointment.statut.toLowerCase())}`}>
                          {appointment.statut}
                        </span>
                        <div className="flex space-x-2">
                          <button className="p-2 text-gray-400 hover:text-blue-600 rounded-lg hover:bg-blue-50">
                            <Video className="w-4 h-4" />
                          </button>
                          <button className="p-2 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-50">
                            <MoreVertical className="w-4 h-4" />
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Rappels santé */}
            <div className="bg-white rounded-xl shadow-sm">
              <div className="p-6 border-b border-gray-200">
                <h3 className="text-lg font-semibold text-gray-900">Rappels santé personnalisés</h3>
              </div>
              <div className="p-6">
                <div className="space-y-3">
                  <div className="flex items-center p-3 bg-blue-50 rounded-lg">
                    <Bell className="w-5 h-5 text-blue-600 mr-3" />
                    <span className="text-sm text-blue-800">Prise de médicament - Antibiotique à 14h</span>
                  </div>
                  <div className="flex items-center p-3 bg-green-50 rounded-lg">
                    <Heart className="w-5 h-5 text-green-600 mr-3" />
                    <span className="text-sm text-green-800">Rappel - Bilan sanguin annuel dans 2 mois</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'appointments' && (
          <div className="space-y-6">
            {/* Appointment Management */}
            <div className="bg-white rounded-xl shadow-sm">
              <div className="p-6 border-b border-gray-200">
                <div className="flex items-center justify-between">
                  <h3 className="text-lg font-semibold text-gray-900">Gestion des rendez-vous</h3>
                  <button className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center">
                    <Plus className="w-4 h-4 mr-2" />
                    Nouveau RDV
                  </button>
                </div>
              </div>
              <div className="divide-y divide-gray-200">
                {loading ? (
                  <div className="flex justify-center py-8">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                  </div>
                ) : myAppointments.map((appointment) => (
                  <div key={appointment.id} className="p-6">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center space-x-4">
                        <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                          <Stethoscope className="w-6 h-6 text-blue-600" />
                        </div>
                        <div>
                          <h4 className="font-medium text-gray-900">Dr. {appointment.medecinPrenom} {appointment.medecinNom}</h4>
                          <p className="text-sm text-gray-600">{appointment.medecinSpecialite}</p>
                          <p className="text-sm text-gray-500">{formatDate(appointment.dateHeureDebut)} à {formatTime(appointment.dateHeureDebut)}</p>
                          <p className="text-sm text-gray-500">{appointment.motifConsultation}</p>
                        </div>
                      </div>
                      <div className="flex items-center space-x-4">
                        <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusBadge(appointment.statut)}`}>
                          {appointment.statut}
                        </span>
                        <div className="flex space-x-2">
                          {appointment.statut === 'PLANIFIE' && (
                            <button 
                              className="text-blue-600 hover:bg-blue-50 px-3 py-1 rounded text-sm"
                              onClick={() => rdvService.updateRendezVousStatut(appointment.id, 'CONFIRME')}
                            >
                              Confirmer
                            </button>
                          )}
                          {(appointment.statut === 'PLANIFIE' || appointment.statut === 'CONFIRME') && (
                            <button 
                              className="text-red-600 hover:bg-red-50 px-3 py-1 rounded text-sm"
                              onClick={() => rdvService.cancelRendezVous(appointment.id, 'Annulé par le patient')}
                            >
                              Annuler
                            </button>
                          )}
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {activeTab === 'doctors' && (
          <div className="space-y-6">
            {/* Search Filters */}
            <div className="bg-white rounded-xl shadow-sm p-6">
              <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-4 sm:space-y-0">
                <div className="relative flex-1 mr-4">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                  <input
                    type="text"
                    placeholder="Rechercher par spécialité, nom..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent w-full"
                  />
                </div>
                <div className="flex space-x-4">
                  <select className="border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                    <option value="all">Toutes spécialités</option>
                    <option value="general">Médecine Générale</option>
                    <option value="cardio">Cardiologie</option>
                    <option value="orl">ORL</option>
                  </select>
                  <select className="border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                    <option value="all">Tous tarifs</option>
                    <option value="low">Moins de 30.000 FCFA</option>
                    <option value="mid">30.000 - 50.000 FCFA</option>
                    <option value="high">Plus de 50.000 FCFA</option>
                  </select>
                </div>
              </div>
            </div>

            {/* Doctors Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {loading ? (
                <div className="col-span-full flex justify-center py-8">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                </div>
              ) : (
                availableDoctors.map((doctor) => (
                  <div key={doctor.id} className="bg-white rounded-xl shadow-sm p-6">
                    <div className="flex items-center mb-4">
                      <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center">
                        <span className="text-blue-600 font-semibold text-lg">
                          {doctor.prenom[0]}{doctor.nom[0]}
                        </span>
                      </div>
                      <div className="ml-4">
                        <h3 className="font-semibold text-gray-900">Dr. {doctor.prenom} {doctor.nom}</h3>
                        <p className="text-sm text-gray-600">{doctor.specialite}</p>
                      </div>
                    </div>
                    <div className="space-y-2 mb-4">
                      <div className="flex items-center text-sm text-gray-600">
                        <MapPin className="w-4 h-4 mr-2" />
                        {doctor.adresseCabinet}, {doctor.codePostalCabinet} {doctor.villeCabinet}
                      </div>
                      <div className="flex items-center text-sm text-gray-600">
                        <Clock className="w-4 h-4 mr-2" />
                        Consultation {doctor.dureeConsultationDefaut}min
                      </div>
                      <div className="text-lg font-semibold text-gray-900">
                        {doctor.tarifConsultation.toLocaleString()} FCFA
                      </div>
                      {doctor.conventionne && (
                        <span className="inline-block bg-green-100 text-green-800 text-xs px-2 py-1 rounded-full">
                          Conventionné
                        </span>
                      )}
                    </div>
                    <div className="flex space-x-2">
                      <button className="flex-1 bg-blue-600 text-white text-sm py-2 px-3 rounded-lg hover:bg-blue-700 transition-colors">
                        Prendre RDV
                      </button>
                      <button className="text-gray-600 hover:bg-gray-50 p-2 rounded-lg border">
                        <Eye className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        )}

        {activeTab === 'health' && (
          <div className="space-y-6">
            {/* Health Records */}
            <div className="bg-white rounded-xl shadow-sm">
              <div className="p-6 border-b border-gray-200">
                <h3 className="text-lg font-semibold text-gray-900">Mon dossier médical</h3>
              </div>
              <div className="divide-y divide-gray-200">
                {healthRecords.map((record) => (
                  <div key={record.id} className="p-6">
                    <div className="flex items-start justify-between">
                      <div className="flex items-start space-x-4">
                        <div className="p-2 rounded-lg bg-gray-50">
                          {getRecordTypeIcon(record.type)}
                        </div>
                        <div className="flex-1">
                          <h4 className="font-medium text-gray-900">{record.title}</h4>
                          <p className="text-sm text-gray-600">{record.doctor}</p>
                          <p className="text-sm text-gray-500">{formatDate(record.date)}</p>
                          <p className="text-sm text-gray-700 mt-2">{record.content}</p>
                        </div>
                      </div>
                      <button className="text-gray-400 hover:text-gray-600">
                        <MoreVertical className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {activeTab === 'ai-chat' && (
          <div className="bg-white rounded-xl shadow-sm h-96 flex flex-col">
            <div className="p-6 border-b border-gray-200">
              <h3 className="text-lg font-semibold text-gray-900 flex items-center">
                <Bot className="w-6 h-6 mr-2 text-blue-600" />
                Assistant Médical IA
              </h3>
              <p className="text-sm text-gray-600 mt-1">Décrivez vos symptômes pour obtenir des conseils personnalisés</p>
            </div>
            
            <div className="flex-1 overflow-y-auto p-6">
              <div className="space-y-4">
                {chatMessages.map((message) => (
                  <div key={message.id} className={`flex ${message.isBot ? 'justify-start' : 'justify-end'}`}>
                    <div className={`max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${
                      message.isBot 
                        ? 'bg-blue-100 text-blue-900' 
                        : 'bg-gray-100 text-gray-900'
                    }`}>
                      <p className="text-sm">{message.text}</p>
                      <span className="text-xs opacity-75">{message.timestamp}</span>
                      {message.suggestions && (
                        <div className="mt-2 space-y-1">
                          {message.suggestions.map((suggestion, idx) => (
                            <button
                              key={idx}
                              onClick={() => handleSuggestionClick(suggestion)}
                              className="block w-full text-left text-xs bg-white bg-opacity-50 hover:bg-opacity-75 px-2 py-1 rounded transition-colors"
                            >
                              {suggestion}
                            </button>
                          ))}
                        </div>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="p-6 border-t border-gray-200">
              <div className="flex space-x-2">
                <input
                  type="text"
                  value={chatInput}
                  onChange={(e) => setChatInput(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
                  placeholder="Décrivez vos symptômes..."
                  className="flex-1 border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
                <button
                  onClick={handleSendMessage}
                  className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
                >
                  Envoyer
                </button>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'follow-up' && (
          <div className="space-y-6">
            {/* Personalized Follow-up */}
            <div className="bg-white rounded-xl shadow-sm">
              <div className="p-6 border-b border-gray-200">
                <h3 className="text-lg font-semibold text-gray-900">Suivi personnalisé post-consultation</h3>
              </div>
              <div className="p-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="space-y-4">
                    <h4 className="font-medium text-gray-900">Rappels de traitement</h4>
                    <div className="space-y-2">
                      <div className="flex items-center p-3 bg-green-50 rounded-lg">
                        <Pill className="w-5 h-5 text-green-600 mr-3" />
                        <div>
                          <p className="text-sm font-medium text-green-800">Antibiotique - 14h00</p>
                          <p className="text-xs text-green-600">Amoxicilline 1g - J3/7</p>
                        </div>
                      </div>
                      <div className="flex items-center p-3 bg-blue-50 rounded-lg">
                        <Heart className="w-5 h-5 text-blue-600 mr-3" />
                        <div>
                          <p className="text-sm font-medium text-blue-800">Tension artérielle - 18h00</p>
                          <p className="text-xs text-blue-600">Mesure quotidienne</p>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div className="space-y-4">
                    <h4 className="font-medium text-gray-900">Conseils santé personnalisés</h4>
                    <div className="space-y-2">
                      <div className="p-3 bg-orange-50 rounded-lg">
                        <p className="text-sm font-medium text-orange-800">Régime alimentaire</p>
                        <p className="text-xs text-orange-600">Éviter les aliments épicés pendant 1 semaine</p>
                      </div>
                      <div className="p-3 bg-purple-50 rounded-lg">
                        <p className="text-sm font-medium text-purple-800">Activité physique</p>
                        <p className="text-xs text-purple-600">Marche 30min/jour recommandée</p>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="mt-6 p-4 bg-gray-50 rounded-lg">
                  <h4 className="font-medium text-gray-900 mb-2">Prochaine étape recommandée par l'IA</h4>
                  <p className="text-sm text-gray-700">
                    Basé sur votre suivi actuel, il est recommandé de programmer un contrôle avec Dr. Martin dans 5 jours 
                    pour vérifier l'efficacité du traitement.
                  </p>
                  <button className="mt-2 bg-blue-600 text-white text-sm px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors">
                    Programmer le contrôle
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default PatientDashboard;