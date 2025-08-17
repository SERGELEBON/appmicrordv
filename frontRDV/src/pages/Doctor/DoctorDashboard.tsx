import React, { useState, useEffect } from 'react';
import { 
  Calendar, 
  Clock, 
  Users, 
  FileText, 
  Stethoscope,
  MessageSquare,
  CheckCircle,
  XCircle,
  AlertCircle,
  Phone,
  Video,
  UserCheck,
  Activity,
  TrendingUp,
  Bell,
  Search,
  Filter,
  MoreVertical,
  Plus,
  Edit,
  Trash2,
  Eye
} from 'lucide-react';
import { rdvService, type RendezVous as RdvType, type Patient as PatientType } from '../../services/rdvService';
import { useAuth } from '../../context/AuthContext';

interface Patient {
  id: number;
  name: string;
  age: number;
  gender: 'M' | 'F';
  phone: string;
  email: string;
  lastVisit: string;
  nextAppointment?: string;
  status: 'active' | 'inactive';
}

interface Appointment {
  id: number;
  patientName: string;
  patientId: number;
  date: string;
  time: string;
  type: 'consultation' | 'follow-up' | 'emergency';
  status: 'scheduled' | 'completed' | 'cancelled' | 'in-progress';
  reason: string;
  duration: number; // en minutes
  notes?: string;
}

interface ConsultationSummary {
  id: number;
  patientName: string;
  date: string;
  duration: number;
  diagnosis: string;
  treatment: string;
  nextSteps: string;
  aiGenerated: boolean;
}

const DoctorDashboard = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState<'overview' | 'appointments' | 'patients' | 'consultations' | 'ai-assistant'>('overview');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const [appointments, setAppointments] = useState<RdvType[]>([]);
  const [patients, setPatients] = useState<PatientType[]>([]);
  const [loading, setLoading] = useState(true);

  // Charger les données depuis l'API
  useEffect(() => {
    const loadData = async () => {
      if (!user?.id) return;
      
      try {
        setLoading(true);
        const [rdvData, patientData] = await Promise.all([
          rdvService.getRendezVousByMedecin(user.id),
          rdvService.getAllPatients()
        ]);
        
        setAppointments(rdvData);
        setPatients(patientData);
      } catch (error) {
        console.error('Erreur lors du chargement des données:', error);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [user?.id]);

  const consultationSummaries: ConsultationSummary[] = [
    {
      id: 1,
      patientName: 'Sophie Laurent',
      date: '2025-08-14',
      duration: 45,
      diagnosis: 'Gastrite légère',
      treatment: 'Anti-inflammatoire + régime alimentaire',
      nextSteps: 'Contrôle dans 2 semaines',
      aiGenerated: true
    },
    {
      id: 2,
      patientName: 'Paul Durand',
      date: '2025-08-13',
      duration: 30,
      diagnosis: 'Grippe saisonnière',
      treatment: 'Repos + paracétamol',
      nextSteps: 'Retour si symptômes persistent',
      aiGenerated: true
    }
  ];

  const todayStats = {
    scheduledAppointments: 8,
    completedAppointments: 5,
    cancelledAppointments: 1,
    totalPatients: 24,
    avgConsultationTime: 28 // minutes
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  };

  const formatTime = (timeString: string) => {
    return new Date(`2000-01-01T${timeString}`).toLocaleTimeString('fr-FR', {
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getStatusBadge = (status: string) => {
    const statusConfig = {
      scheduled: 'bg-blue-100 text-blue-800',
      completed: 'bg-green-100 text-green-800',
      cancelled: 'bg-red-100 text-red-800',
      'in-progress': 'bg-orange-100 text-orange-800',
      active: 'bg-green-100 text-green-800',
      inactive: 'bg-gray-100 text-gray-800'
    };
    
    return statusConfig[status as keyof typeof statusConfig] || 'bg-gray-100 text-gray-800';
  };

  const getAppointmentTypeIcon = (type: string) => {
    switch (type) {
      case 'consultation': return <Stethoscope className="w-4 h-4" />;
      case 'follow-up': return <UserCheck className="w-4 h-4" />;
      case 'emergency': return <AlertCircle className="w-4 h-4" />;
      default: return <Calendar className="w-4 h-4" />;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Tableau de bord Médecin</h1>
          <p className="text-gray-600 mt-2">Dr. Marie Dupont - Médecine Générale</p>
        </div>

        {/* Navigation Tabs */}
        <div className="border-b border-gray-200 mb-8">
          <nav className="-mb-px flex space-x-8">
            {[
              { id: 'overview', label: 'Vue d\'ensemble', icon: Activity },
              { id: 'appointments', label: 'Rendez-vous', icon: Calendar },
              { id: 'patients', label: 'Mes Patients', icon: Users },
              { id: 'consultations', label: 'Consultations', icon: FileText },
              { id: 'ai-assistant', label: 'Assistant IA', icon: MessageSquare }
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
            {/* Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-6">
              {[
                { title: 'RDV aujourd\'hui', value: todayStats.scheduledAppointments, icon: Calendar, color: 'blue' },
                { title: 'Consultations terminées', value: todayStats.completedAppointments, icon: CheckCircle, color: 'green' },
                { title: 'Annulations', value: todayStats.cancelledAppointments, icon: XCircle, color: 'red' },
                { title: 'Total patients', value: todayStats.totalPatients, icon: Users, color: 'purple' },
                { title: 'Temps moyen', value: `${todayStats.avgConsultationTime}min`, icon: Clock, color: 'orange' }
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

            {/* Agenda du jour */}
            <div className="bg-white rounded-xl shadow-sm">
              <div className="p-6 border-b border-gray-200">
                <h3 className="text-lg font-semibold text-gray-900">Agenda du jour</h3>
              </div>
              <div className="p-6">
                <div className="space-y-4">
                  {appointments.filter(apt => apt.date === selectedDate).map((appointment) => (
                    <div key={appointment.id} className="flex items-center justify-between p-4 border border-gray-200 rounded-lg hover:bg-gray-50">
                      <div className="flex items-center space-x-4">
                        <div className="flex items-center justify-center w-10 h-10 bg-blue-100 rounded-full">
                          {getAppointmentTypeIcon(appointment.type)}
                        </div>
                        <div>
                          <p className="font-medium text-gray-900">{appointment.patientName}</p>
                          <p className="text-sm text-gray-600">{appointment.reason}</p>
                          <p className="text-xs text-gray-500">{formatTime(appointment.time)} - {appointment.duration}min</p>
                        </div>
                      </div>
                      <div className="flex items-center space-x-4">
                        <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusBadge(appointment.status)}`}>
                          {appointment.status}
                        </span>
                        <div className="flex space-x-2">
                          <button className="p-2 text-gray-400 hover:text-blue-600 rounded-lg hover:bg-blue-50">
                            <Phone className="w-4 h-4" />
                          </button>
                          <button className="p-2 text-gray-400 hover:text-green-600 rounded-lg hover:bg-green-50">
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
          </div>
        )}

        {activeTab === 'appointments' && (
          <div className="space-y-6">
            {/* Filters */}
            <div className="bg-white rounded-xl shadow-sm p-6">
              <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-4 sm:space-y-0">
                <div className="flex items-center space-x-4">
                  <input
                    type="date"
                    value={selectedDate}
                    onChange={(e) => setSelectedDate(e.target.value)}
                    className="border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                  <select className="border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                    <option value="all">Tous les statuts</option>
                    <option value="scheduled">Programmé</option>
                    <option value="completed">Terminé</option>
                    <option value="cancelled">Annulé</option>
                  </select>
                </div>
                <button className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center">
                  <Plus className="w-4 h-4 mr-2" />
                  Nouveau RDV
                </button>
              </div>
            </div>

            {/* Appointments Table */}
            <div className="bg-white rounded-xl shadow-sm overflow-hidden">
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Patient</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date & Heure</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Motif</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Statut</th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {appointments.map((appointment) => (
                      <tr key={appointment.id} className="hover:bg-gray-50">
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="font-medium text-gray-900">{appointment.patientName}</div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {formatDate(appointment.date)} à {formatTime(appointment.time)}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="flex items-center">
                            {getAppointmentTypeIcon(appointment.type)}
                            <span className="ml-2 text-sm text-gray-900 capitalize">{appointment.type}</span>
                          </div>
                        </td>
                        <td className="px-6 py-4 text-sm text-gray-500">{appointment.reason}</td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusBadge(appointment.status)}`}>
                            {appointment.status}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                          <div className="flex items-center justify-end space-x-2">
                            <button className="text-gray-400 hover:text-blue-600">
                              <Eye className="w-4 h-4" />
                            </button>
                            <button className="text-gray-400 hover:text-green-600">
                              <Edit className="w-4 h-4" />
                            </button>
                            <button className="text-gray-400 hover:text-red-600">
                              <XCircle className="w-4 h-4" />
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'patients' && (
          <div className="space-y-6">
            {/* Search */}
            <div className="bg-white rounded-xl shadow-sm p-6">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                <input
                  type="text"
                  placeholder="Rechercher un patient..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent w-full"
                />
              </div>
            </div>

            {/* Patients Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {patients.map((patient) => (
                <div key={patient.id} className="bg-white rounded-xl shadow-sm p-6">
                  <div className="flex items-center justify-between mb-4">
                    <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                      <span className="text-blue-600 font-semibold">{patient.name.split(' ').map(n => n[0]).join('')}</span>
                    </div>
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusBadge(patient.status)}`}>
                      {patient.status}
                    </span>
                  </div>
                  <h3 className="font-semibold text-gray-900 mb-1">{patient.name}</h3>
                  <p className="text-sm text-gray-600 mb-2">{patient.age} ans • {patient.gender === 'M' ? 'Homme' : 'Femme'}</p>
                  <p className="text-sm text-gray-500 mb-2">{patient.phone}</p>
                  <p className="text-sm text-gray-500 mb-4">Dernière visite : {formatDate(patient.lastVisit)}</p>
                  {patient.nextAppointment && (
                    <p className="text-sm text-blue-600 font-medium mb-4">
                      Prochain RDV : {formatDate(patient.nextAppointment.split('T')[0])}
                    </p>
                  )}
                  <div className="flex space-x-2">
                    <button className="flex-1 bg-blue-600 text-white text-sm py-2 px-3 rounded-lg hover:bg-blue-700 transition-colors">
                      Voir dossier
                    </button>
                    <button className="text-gray-400 hover:text-blue-600 p-2 rounded-lg hover:bg-blue-50">
                      <Phone className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {activeTab === 'consultations' && (
          <div className="space-y-6">
            {/* Consultation Summaries */}
            <div className="bg-white rounded-xl shadow-sm">
              <div className="p-6 border-b border-gray-200">
                <h3 className="text-lg font-semibold text-gray-900">Résumés de consultation IA</h3>
              </div>
              <div className="divide-y divide-gray-200">
                {consultationSummaries.map((summary) => (
                  <div key={summary.id} className="p-6">
                    <div className="flex items-center justify-between mb-4">
                      <div>
                        <h4 className="font-medium text-gray-900">{summary.patientName}</h4>
                        <p className="text-sm text-gray-500">{formatDate(summary.date)} • {summary.duration}min</p>
                      </div>
                      {summary.aiGenerated && (
                        <span className="bg-purple-100 text-purple-800 text-xs font-medium px-2 py-1 rounded-full">
                          Généré par IA
                        </span>
                      )}
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
                      <div>
                        <p className="font-medium text-gray-700 mb-1">Diagnostic</p>
                        <p className="text-gray-600">{summary.diagnosis}</p>
                      </div>
                      <div>
                        <p className="font-medium text-gray-700 mb-1">Traitement</p>
                        <p className="text-gray-600">{summary.treatment}</p>
                      </div>
                      <div>
                        <p className="font-medium text-gray-700 mb-1">Prochaines étapes</p>
                        <p className="text-gray-600">{summary.nextSteps}</p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {activeTab === 'ai-assistant' && (
          <div className="space-y-6">
            {/* AI Assistant */}
            <div className="bg-white rounded-xl shadow-sm p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Assistant Médical IA</h3>
              <div className="bg-gray-50 rounded-lg p-4 mb-4">
                <p className="text-gray-600 text-center">
                  L'assistant IA pour résumé automatique des consultations sera disponible prochainement.
                </p>
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="border border-gray-200 rounded-lg p-4">
                  <h4 className="font-medium text-gray-900 mb-2">🎙️ Transcription automatique</h4>
                  <p className="text-sm text-gray-600">Enregistrez vos consultations et laissez l'IA générer automatiquement le résumé.</p>
                </div>
                <div className="border border-gray-200 rounded-lg p-4">
                  <h4 className="font-medium text-gray-900 mb-2">📋 Notes structurées</h4>
                  <p className="text-sm text-gray-600">Génération automatique de notes médicales structurées pour le dossier patient.</p>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default DoctorDashboard;