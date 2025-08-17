import { authApi } from './api';
import { 
  LoginRequest, 
  RegisterRequest, 
  JwtResponse, 
  User, 
  MessageResponse 
} from '../types/auth';

export class AuthService {
  // Connexion utilisateur
  async login(credentials: LoginRequest): Promise<JwtResponse> {
    try {
      // Le backend accepte maintenant email ou username dans le champ "email"
      const loginData = {
        email: credentials.username, // Peut être un email ou un username
        password: credentials.password
      };
      const response = await authApi.post<JwtResponse>('/auth/signin', loginData);
      
      // Stocker le token dans localStorage
      if (response.accessToken) {
        localStorage.setItem('authToken', response.accessToken);
        localStorage.setItem('refreshToken', response.refreshToken);
        localStorage.setItem('user', JSON.stringify({
          id: response.id,
          username: response.username,
          email: response.email,
          roles: response.roles
        }));
      }
      
      return response;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  }

  // Inscription utilisateur
  async register(userData: RegisterRequest): Promise<MessageResponse> {
    try {
      const response = await authApi.post<MessageResponse>('/auth/signup', userData);
      return response;
    } catch (error) {
      console.error('Registration error:', error);
      throw error;
    }
  }

  // Déconnexion
  async logout(): Promise<void> {
    try {
      await authApi.post('/auth/signout');
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      // Nettoyer le localStorage même en cas d'erreur
      localStorage.removeItem('authToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
    }
  }

  // Rafraîchir le token
  async refreshToken(): Promise<JwtResponse> {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    try {
      const response = await authApi.post<JwtResponse>('/auth/refresh', {
        refreshToken
      });

      // Mettre à jour les tokens
      localStorage.setItem('authToken', response.accessToken);
      if (response.refreshToken) {
        localStorage.setItem('refreshToken', response.refreshToken);
      }

      return response;
    } catch (error) {
      // En cas d'erreur, déconnecter l'utilisateur
      this.logout();
      throw error;
    }
  }

  // Valider le token
  async validateToken(): Promise<boolean> {
    const token = localStorage.getItem('authToken');
    if (!token) {
      return false;
    }

    try {
      const response = await authApi.post('/auth/validate', { token });
      return response.valid === true;
    } catch (error) {
      console.error('Token validation error:', error);
      return false;
    }
  }

  // Obtenir l'utilisateur actuel depuis localStorage
  getCurrentUser(): User | null {
    const userStr = localStorage.getItem('user');
    if (!userStr) return null;

    try {
      return JSON.parse(userStr);
    } catch (error) {
      console.error('Error parsing user data:', error);
      return null;
    }
  }

  // Vérifier si l'utilisateur est connecté
  isAuthenticated(): boolean {
    const token = localStorage.getItem('authToken');
    const user = this.getCurrentUser();
    return !!(token && user);
  }

  // Obtenir le token d'accès
  getAccessToken(): string | null {
    return localStorage.getItem('authToken');
  }
}

// Instance singleton du service d'authentification
export const authService = new AuthService();