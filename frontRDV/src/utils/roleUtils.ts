import { User } from '../types/auth';

export const getRedirectPath = (user: User): string => {
  // Vérifier si l'utilisateur a le rôle ADMIN
  if (user.roles && user.roles.includes('ADMIN')) {
    return '/admin';
  }
  
  // Vérifier si l'utilisateur a le rôle MEDECIN
  if (user.roles && user.roles.includes('MEDECIN')) {
    return '/dashboard';
  }
  
  // Vérifier si l'utilisateur a le rôle PATIENT
  if (user.roles && user.roles.includes('PATIENT')) {
    return '/patient';
  }
  
  // Par défaut, rediriger vers la page d'accueil
  return '/';
};

export const hasRole = (user: User | null, role: string): boolean => {
  return user?.roles?.includes(role) || false;
};

export const isAdmin = (user: User | null): boolean => {
  return hasRole(user, 'ADMIN');
};

export const isMedecin = (user: User | null): boolean => {
  return hasRole(user, 'MEDECIN');
};

export const isPatient = (user: User | null): boolean => {
  return hasRole(user, 'PATIENT');
};