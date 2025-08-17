import { rdvApi } from './api';

export interface StatistiquesGlobales {
  totalRendezVous: number;
  rendezVousPlanifies: number;
  rendezVousConfirmes: number;
  rendezVousTermines: number;
  rendezVousAnnules: number;
  totalPatients: number;
  totalMedecins: number;
  medecinActifs: number;
  patientActifs: number;
  rendezVousAujourdhui: number;
  nouveauxPatientsAujourdhui: number;
}

export interface StatistiquesPeriode {
  rendezVousPeriode: number;
  nouveauxPatientsPeriode: number;
  nouveauxMedecinsPeriode: number;
}

export interface DashboardStats {
  globales: StatistiquesGlobales;
  semaine: StatistiquesPeriode;
  mois: StatistiquesPeriode;
  evolutionMensuelle: Record<string, number>;
}

export interface ActiviteRecente {
  rdvCreesRecemment: number;
  nouveauxPatientsRecemment: number;
  nouveauxMedecinsRecemment: number;
  derniersRendezVous: any[];
}

class AdminService {
  async getStatistiquesGlobales(): Promise<StatistiquesGlobales> {
    return rdvApi.get<StatistiquesGlobales>('/admin/stats/globales');
  }

  async getStatistiquesPeriode(debut: string, fin: string): Promise<StatistiquesPeriode> {
    return rdvApi.get<StatistiquesPeriode>(`/admin/stats/periode?debut=${debut}&fin=${fin}`);
  }

  async getStatistiquesMedecins(): Promise<any> {
    return rdvApi.get('/admin/stats/medecins');
  }

  async getDashboardStats(): Promise<DashboardStats> {
    return rdvApi.get<DashboardStats>('/admin/stats/dashboard');
  }

  async getActiviteRecente(): Promise<ActiviteRecente> {
    return rdvApi.get<ActiviteRecente>('/admin/stats/activite-recente');
  }

  // Calculs de ratios
  calculateRatios(stats: StatistiquesGlobales) {
    const total = stats.totalRendezVous;
    if (total === 0) {
      return {
        tauxAnnulation: 0,
        tauxCompletion: 0,
        tauxConfirmation: 0
      };
    }

    return {
      tauxAnnulation: Math.round((stats.rendezVousAnnules / total) * 100),
      tauxCompletion: Math.round((stats.rendezVousTermines / total) * 100),
      tauxConfirmation: Math.round((stats.rendezVousConfirmes / total) * 100)
    };
  }

  // Formatage des données pour graphiques
  formatEvolutionData(evolutionMensuelle: Record<string, number>) {
    return Object.entries(evolutionMensuelle).map(([mois, valeur]) => ({
      mois: mois.substring(0, 3), // Abréger le nom du mois
      rendezVous: valeur
    }));
  }
}

export const adminService = new AdminService();