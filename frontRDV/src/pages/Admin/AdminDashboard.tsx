import React, { useState, useEffect } from 'react';
import { 
  Users, 
  Calendar, 
  BarChart3, 
  Settings, 
  UserPlus, 
  Activity,
  Clock,
  TrendingUp,
  Filter,
  Search,
  MoreVertical,
  Eye,
  Edit,
  Trash2,
  Star
} from 'lucide-react';
import { adminService, type StatistiquesGlobales, type DashboardStats } from '../../services/adminService';
import { rdvService } from '../../services/rdvService';

interface User {
  id: number;
  name: string;
  email: string;
  role: 'PATIENT' | 'DOCTOR' | 'ADMIN';
  status: 'active' | 'inactive';
  lastActivity: string;
  avatar?: string;
}

interface Appointment {
  id: number;
  patientName: string;
  doctorName: string;
  date: string;
  time: string;
  status: 'scheduled' | 'completed' | 'cancelled' | 'priority';
  priority?: number;
}

const AdminDashboard = () => {
  const [activeTab, setActiveTab] = useState<'overview' | 'users' | 'appointments' | 'analytics'>('overview');
  const [searchTerm, setSearchTerm] = useState('');
  const [filterRole, setFilterRole] = useState<string>('all');
  const [dashboardStats, setDashboardStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [users, setUsers] = useState<User[]>([]);
  const [appointments, setAppointments] = useState<Appointment[]>([]);

  // Charger les statistiques depuis l'API
  useEffect(() => {
    const loadStats = async () => {
      try {
        setLoading(true);
        const stats = await adminService.getDashboardStats();
        setDashboardStats(stats);
        
        // Charger aussi les rendez-vous récents
        const recentAppointments = await rdvService.getAllRendezVous();
        setAppointments(recentAppointments.slice(0, 10));
      } catch (error) {
        console.error('Erreur lors du chargement des statistiques:', error);
      } finally {
        setLoading(false);
      }
    };

    loadStats();
  }, []);

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
      active: 'bg-green-100 text-green-800',
      inactive: 'bg-gray-100 text-gray-800',
      scheduled: 'bg-blue-100 text-blue-800',
      completed: 'bg-green-100 text-green-800',
      cancelled: 'bg-red-100 text-red-800',
      priority: 'bg-orange-100 text-orange-800'
    };
    
    return statusConfig[status as keyof typeof statusConfig] || 'bg-gray-100 text-gray-800';
  };

  // Filtrer les utilisateurs selon le terme de recherche et le rôle
  const filteredUsers = users.filter(user => {
    const matchesSearch = user.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         user.email.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesRole = filterRole === 'all' || user.role === filterRole;
    return matchesSearch && matchesRole;
  });

  // Données de statistiques statiques pour l'onglet Analytics
  const stats = {
    weeklyGrowth: 12,
    completionRate: 89,
    totalPatients: dashboardStats?.globales.totalPatients || 0
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Administration</h1>
          <p className="text-gray-600 mt-2">Tableau de bord administrateur - RDV360</p>
        </div>

        {/* Navigation Tabs */}
        <div className="border-b border-gray-200 mb-8">
          <nav className="-mb-px flex space-x-8">
            {[
              { id: 'overview', label: 'Vue d\'ensemble', icon: BarChart3 },
              { id: 'users', label: 'Utilisateurs', icon: Users },
              { id: 'appointments', label: 'Rendez-vous', icon: Calendar },
              { id: 'analytics', label: 'Statistiques', icon: Activity }
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
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              {loading ? (
                <div className="col-span-full flex justify-center py-8">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                </div>
              ) : dashboardStats ? [
                { title: 'Total Patients', value: dashboardStats.globales.totalPatients.toLocaleString(), icon: Users, color: 'blue' },
                { title: 'Total Médecins', value: dashboardStats.globales.totalMedecins, icon: UserPlus, color: 'green' },
                { title: 'RDV aujourd\'hui', value: dashboardStats.globales.rendezVousAujourdhui, icon: Calendar, color: 'purple' },
                { title: 'RDV terminés', value: dashboardStats.globales.rendezVousTermines, icon: TrendingUp, color: 'orange' }
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
              }) : []}
            </div>

            {/* Statistiques détaillées */}
            {!loading && dashboardStats && (
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="bg-white rounded-xl shadow-sm p-6">
                  <h3 className="text-lg font-semibold text-gray-900 mb-4">Statuts RDV</h3>
                  <div className="space-y-3">
                    <div className="flex justify-between">
                      <span className="text-gray-600">Planifiés</span>
                      <span className="font-semibold">{dashboardStats.globales.rendezVousPlanifies}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Confirmés</span>
                      <span className="font-semibold">{dashboardStats.globales.rendezVousConfirmes}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Terminés</span>
                      <span className="font-semibold">{dashboardStats.globales.rendezVousTermines}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Annulés</span>
                      <span className="font-semibold text-red-600">{dashboardStats.globales.rendezVousAnnules}</span>
                    </div>
                  </div>
                </div>

                <div className="bg-white rounded-xl shadow-sm p-6">
                  <h3 className="text-lg font-semibold text-gray-900 mb-4">Activité</h3>
                  <div className="space-y-3">
                    <div className="flex justify-between">
                      <span className="text-gray-600">Médecins actifs</span>
                      <span className="font-semibold">{dashboardStats.globales.medecinActifs}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Patients actifs</span>
                      <span className="font-semibold">{dashboardStats.globales.patientActifs}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Nouveaux patients</span>
                      <span className="font-semibold text-green-600">{dashboardStats.globales.nouveauxPatientsAujourdhui}</span>
                    </div>
                  </div>
                </div>

                <div className="bg-white rounded-xl shadow-sm p-6">
                  <h3 className="text-lg font-semibold text-gray-900 mb-4">Performance</h3>
                  <div className="space-y-3">
                    <div className="flex justify-between">
                      <span className="text-gray-600">Total RDV</span>
                      <span className="font-semibold">{dashboardStats.globales.totalRendezVous}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Ce mois</span>
                      <span className="font-semibold">{dashboardStats.mois.rendezVousPeriode}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Cette semaine</span>
                      <span className="font-semibold text-blue-600">{dashboardStats.semaine.rendezVousPeriode}</span>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Recent Activity */}
            <div className="bg-white rounded-xl shadow-sm">
              <div className="p-6 border-b border-gray-200">
                <h3 className="text-lg font-semibold text-gray-900">Activité récente</h3>
              </div>
              <div className="p-6">
                <div className="space-y-4">
                  {appointments.slice(0, 5).map((appointment) => (
                    <div key={appointment.id} className="flex items-center justify-between py-3">
                      <div className="flex items-center">
                        <div className="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center">
                          <Calendar className="w-5 h-5 text-gray-600" />
                        </div>
                        <div className="ml-4">
                          <p className="font-medium text-gray-900">
                            {appointment.patientName} - {appointment.doctorName}
                          </p>
                          <p className="text-sm text-gray-600">
                            {formatDate(appointment.date)} à {formatTime(appointment.time)}
                          </p>
                        </div>
                      </div>
                      <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusBadge(appointment.status)}`}>
                        {appointment.status === 'priority' ? (
                          <div className="flex items-center">
                            <Star className="w-3 h-3 mr-1" />
                            Priorité {appointment.priority}%
                          </div>
                        ) : (
                          appointment.status
                        )}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'users' && (
          <div className="space-y-6">
            {/* Search and Filter */}
            <div className="bg-white rounded-xl shadow-sm p-6">
              <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-4 sm:space-y-0">
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                  <input
                    type="text"
                    placeholder="Rechercher des utilisateurs..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent w-full sm:w-64"
                  />
                </div>
                <div className="flex items-center space-x-4">
                  <select
                    value={filterRole}
                    onChange={(e) => setFilterRole(e.target.value)}
                    className="border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  >
                    <option value="all">Tous les rôles</option>
                    <option value="PATIENT">Patients</option>
                    <option value="DOCTOR">Médecins</option>
                    <option value="ADMIN">Administrateurs</option>
                  </select>
                  <button className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center">
                    <UserPlus className="w-4 h-4 mr-2" />
                    Ajouter
                  </button>
                </div>
              </div>
            </div>

            {/* Users Table */}
            <div className="bg-white rounded-xl shadow-sm overflow-hidden">
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Utilisateur
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Rôle
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Statut
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Dernière activité
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {filteredUsers.map((user) => (
                      <tr key={user.id} className="hover:bg-gray-50">
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="flex items-center">
                            <div className="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center">
                              <span className="text-sm font-medium text-gray-700">
                                {user.name.split(' ').map(n => n[0]).join('')}
                              </span>
                            </div>
                            <div className="ml-4">
                              <div className="text-sm font-medium text-gray-900">{user.name}</div>
                              <div className="text-sm text-gray-500">{user.email}</div>
                            </div>
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                            user.role === 'DOCTOR' ? 'bg-blue-100 text-blue-800' :
                            user.role === 'ADMIN' ? 'bg-purple-100 text-purple-800' :
                            'bg-green-100 text-green-800'
                          }`}>
                            {user.role}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusBadge(user.status)}`}>
                            {user.status}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {new Date(user.lastActivity).toLocaleDateString('fr-FR')}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                          <div className="flex items-center justify-end space-x-2">
                            <button className="text-gray-400 hover:text-gray-600">
                              <Eye className="w-4 h-4" />
                            </button>
                            <button className="text-gray-400 hover:text-gray-600">
                              <Edit className="w-4 h-4" />
                            </button>
                            <button className="text-gray-400 hover:text-red-600">
                              <Trash2 className="w-4 h-4" />
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

        {activeTab === 'appointments' && (
          <div className="bg-white rounded-xl shadow-sm">
            <div className="p-6 border-b border-gray-200">
              <div className="flex items-center justify-between">
                <h3 className="text-lg font-semibold text-gray-900">Gestion des rendez-vous</h3>
                <div className="flex items-center space-x-4">
                  <button className="bg-orange-600 text-white px-4 py-2 rounded-lg hover:bg-orange-700 transition-colors flex items-center">
                    <Star className="w-4 h-4 mr-2" />
                    IA Priorité
                  </button>
                  <button className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center">
                    <Calendar className="w-4 h-4 mr-2" />
                    Nouveau RDV
                  </button>
                </div>
              </div>
            </div>
            <div className="p-6">
              <div className="space-y-4">
                {appointments.map((appointment) => (
                  <div key={appointment.id} className="flex items-center justify-between p-4 border border-gray-200 rounded-lg hover:bg-gray-50">
                    <div className="flex items-center space-x-4">
                      <div className={`w-4 h-4 rounded-full ${
                        appointment.status === 'priority' ? 'bg-orange-500' :
                        appointment.status === 'scheduled' ? 'bg-blue-500' :
                        appointment.status === 'completed' ? 'bg-green-500' :
                        'bg-gray-500'
                      }`}></div>
                      <div>
                        <p className="font-medium text-gray-900">
                          {appointment.patientName} → {appointment.doctorName}
                        </p>
                        <p className="text-sm text-gray-600">
                          {formatDate(appointment.date)} à {formatTime(appointment.time)}
                        </p>
                      </div>
                    </div>
                    <div className="flex items-center space-x-4">
                      {appointment.priority && (
                        <div className="flex items-center text-orange-600">
                          <Star className="w-4 h-4 mr-1" />
                          <span className="text-sm font-medium">{appointment.priority}%</span>
                        </div>
                      )}
                      <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusBadge(appointment.status)}`}>
                        {appointment.status}
                      </span>
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

        {activeTab === 'analytics' && (
          <div className="space-y-6">
            <div className="bg-white rounded-xl shadow-sm p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Statistiques détaillées</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <div className="text-center">
                  <div className="text-3xl font-bold text-blue-600">{stats.weeklyGrowth}%</div>
                  <div className="text-sm text-gray-600">Croissance hebdomadaire</div>
                </div>
                <div className="text-center">
                  <div className="text-3xl font-bold text-green-600">{stats.completionRate}%</div>
                  <div className="text-sm text-gray-600">Taux de réussite</div>
                </div>
                <div className="text-center">
                  <div className="text-3xl font-bold text-purple-600">{stats.totalPatients}</div>
                  <div className="text-sm text-gray-600">Patients actifs</div>
                </div>
              </div>
              <div className="mt-8">
                <p className="text-gray-600">
                  Les graphiques détaillés et l'analyse prédictive de l'IA seront disponibles dans la prochaine mise à jour.
                </p>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminDashboard;