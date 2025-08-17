import React, { createContext, useContext, useReducer, useEffect, ReactNode } from 'react';
import { AuthContextType, User, LoginRequest, RegisterRequest } from '../types/auth';
import { authService } from '../services/authService';
import { cleanErrorMessage } from '../utils/errorUtils';

// Actions pour le reducer
type AuthAction =
  | { type: 'AUTH_START' }
  | { type: 'AUTH_SUCCESS'; payload: { user: User; token: string } }
  | { type: 'AUTH_FAILURE'; payload: string }
  | { type: 'LOGOUT' }
  | { type: 'SET_LOADING'; payload: boolean };

// État initial
interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
}

const initialState: AuthState = {
  user: null,
  token: null,
  isAuthenticated: false,
  isLoading: true,
  error: null,
};

// Reducer pour gérer l'état d'authentification
function authReducer(state: AuthState, action: AuthAction): AuthState {
  switch (action.type) {
    case 'AUTH_START':
      return {
        ...state,
        isLoading: true,
        error: null,
      };
    case 'AUTH_SUCCESS':
      return {
        ...state,
        user: action.payload.user,
        token: action.payload.token,
        isAuthenticated: true,
        isLoading: false,
        error: null,
      };
    case 'AUTH_FAILURE':
      return {
        ...state,
        user: null,
        token: null,
        isAuthenticated: false,
        isLoading: false,
        error: action.payload,
      };
    case 'LOGOUT':
      return {
        ...state,
        user: null,
        token: null,
        isAuthenticated: false,
        isLoading: false,
        error: null,
      };
    case 'SET_LOADING':
      return {
        ...state,
        isLoading: action.payload,
      };
    default:
      return state;
  }
}

// Création du contexte
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Provider component
interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, initialState);

  // Vérifier l'authentification au chargement
  useEffect(() => {
    const initializeAuth = async () => {
      const token = authService.getAccessToken();
      const user = authService.getCurrentUser();

      if (token && user) {
        // Valider le token avec le serveur
        try {
          const isValid = await authService.validateToken();
          if (isValid) {
            dispatch({
              type: 'AUTH_SUCCESS',
              payload: { user, token },
            });
          } else {
            // Token invalide, déconnecter
            await authService.logout();
            dispatch({ type: 'LOGOUT' });
          }
        } catch (error) {
          console.error('Token validation failed:', error);
          await authService.logout();
          dispatch({ type: 'LOGOUT' });
        }
      } else {
        dispatch({ type: 'SET_LOADING', payload: false });
      }
    };

    initializeAuth();
  }, []);

  // Fonction de connexion
  const login = async (credentials: LoginRequest): Promise<User> => {
    dispatch({ type: 'AUTH_START' });
    
    try {
      const response = await authService.login(credentials);
      const user: User = {
        id: response.id,
        username: response.username,
        email: response.email,
        roles: response.roles,
        isEnabled: true,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };

      dispatch({
        type: 'AUTH_SUCCESS',
        payload: { user, token: response.accessToken },
      });
      
      return user; // Retourner l'utilisateur pour la redirection
    } catch (error) {
      const rawError = error instanceof Error ? error.message : 'Login failed';
      const cleanError = cleanErrorMessage(rawError);
      dispatch({ type: 'AUTH_FAILURE', payload: cleanError });
      throw new Error(cleanError);
    }
  };

  // Fonction d'inscription
  const register = async (userData: RegisterRequest): Promise<void> => {
    dispatch({ type: 'AUTH_START' });
    
    try {
      await authService.register(userData);
      // Inscription réussie, ne pas connecter automatiquement
      // L'utilisateur sera redirigé vers la page de confirmation
      dispatch({ type: 'SET_LOADING', payload: false });
    } catch (error) {
      const rawError = error instanceof Error ? error.message : 'Registration failed';
      const cleanError = cleanErrorMessage(rawError);
      dispatch({ type: 'AUTH_FAILURE', payload: cleanError });
      throw new Error(cleanError);
    }
  };

  // Fonction de déconnexion
  const logout = async (): Promise<void> => {
    try {
      await authService.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      dispatch({ type: 'LOGOUT' });
    }
  };

  const contextValue: AuthContextType = {
    user: state.user,
    token: state.token,
    isAuthenticated: state.isAuthenticated,
    isLoading: state.isLoading,
    login,
    register,
    logout,
  };

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
};

// Hook personnalisé pour utiliser le contexte d'authentification
export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};