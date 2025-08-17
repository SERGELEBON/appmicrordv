import { rdvApi } from './api';

export interface RendezVous {
  id: number;
  patientId: number;
  patientNom: string;
  patientPrenom: string;
  medecinId: number;
  medecinNom: string;
  medecinPrenom: string;
  medecinSpecialite: string;
  dateHeureDebut: string;
  dateHeureFin: string;
  motifConsultation: string;
  statut: 'PLANIFIE' | 'CONFIRME' | 'EN_COURS' | 'TERMINE' | 'ANNULE' | 'ABSENT';
  notes?: string;
  tarif?: number;
  rappelEnvoye: boolean;
  dateCreation: string;
}

export interface CreateRendezVousRequest {
  patientId: number;
  medecinId: number;
  dateHeureDebut: string;
  dateHeureFin: string;
  motifConsultation: string;
  notes?: string;
}

export interface Medecin {
  id: number;
  nom: string;
  prenom: string;
  numeroRPPS: string;
  specialite: string;
  email: string;
  telephone: string;
  adresseCabinet: string;
  codePostalCabinet: string;
  villeCabinet: string;
  telephoneCabinet: string;
  dureeConsultationDefaut: number;
  tarifConsultation: number;
  conventionne: boolean;
  carteVitaleAcceptee: boolean;
  actif: boolean;
}

export interface Patient {
  id: number;
  nom: string;
  prenom: string;
  dateNaissance: string;
  numeroSecuriteSociale: string;
  email: string;
  telephone: string;
  adresse: string;
  codePostal: string;
  ville: string;
  allergies?: string;
  antecedentsMedicaux?: string;
  actif: boolean;
}

class RdvService {
  // Rendez-vous
  async getRendezVousByPatient(patientId: number): Promise<RendezVous[]> {
    return rdvApi.get<RendezVous[]>(`/rdv/patient/${patientId}`);
  }

  async getRendezVousByMedecin(medecinId: number): Promise<RendezVous[]> {
    return rdvApi.get<RendezVous[]>(`/rdv/medecin/${medecinId}`);
  }

  async createRendezVous(data: CreateRendezVousRequest): Promise<RendezVous> {
    return rdvApi.post<RendezVous>('/rdv', data);
  }

  async updateRendezVousStatut(id: number, statut: string): Promise<RendezVous> {
    return rdvApi.patch<RendezVous>(`/rdv/${id}/statut?nouveauStatut=${statut}`);
  }

  async cancelRendezVous(id: number, motif: string): Promise<void> {
    return rdvApi.patch<void>(`/rdv/${id}/annuler?motifAnnulation=${encodeURIComponent(motif)}`);
  }

  async getCreneauxLibres(medecinId: number, date: string, duree: number = 30): Promise<string[]> {
    return rdvApi.get<string[]>(`/rdv/creneaux-libres?medecinId=${medecinId}&date=${date}&dureeEnMinutes=${duree}`);
  }

  // Obtenir tous les rendez-vous (pour admin)
  async getAllRendezVous(): Promise<RendezVous[]> {
    return rdvApi.get<RendezVous[]>('/rdv');
  }

  // Médecins
  async getAllMedecins(): Promise<Medecin[]> {
    return rdvApi.get<Medecin[]>('/medecins');
  }

  async getMedecinById(id: number): Promise<Medecin> {
    return rdvApi.get<Medecin>(`/medecins/${id}`);
  }

  async getMedecinsBySpecialite(specialite: string): Promise<Medecin[]> {
    return rdvApi.get<Medecin[]>(`/medecins/specialite/${specialite}`);
  }

  // Patients
  async getAllPatients(): Promise<Patient[]> {
    return rdvApi.get<Patient[]>('/patients');
  }

  async getPatientById(id: number): Promise<Patient> {
    return rdvApi.get<Patient>(`/patients/${id}`);
  }

  async createPatient(data: Omit<Patient, 'id' | 'dateCreation' | 'dateModification'>): Promise<Patient> {
    return rdvApi.post<Patient>('/patients', data);
  }

  async updatePatient(id: number, data: Partial<Patient>): Promise<Patient> {
    return rdvApi.put<Patient>(`/patients/${id}`, data);
  }

  // Statistiques
  async getStatistiques() {
    const [planifies, confirmes, termines] = await Promise.all([
      rdvApi.get<number>('/rdv/statistiques/PLANIFIE'),
      rdvApi.get<number>('/rdv/statistiques/CONFIRME'), 
      rdvApi.get<number>('/rdv/statistiques/TERMINE')
    ]);

    return {
      planifies,
      confirmes,
      termines,
      total: planifies + confirmes + termines
    };
  }
}

export const rdvService = new RdvService();