import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Eye, EyeOff, Mail, Lock, Calendar } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { cleanErrorMessage } from '../../utils/errorUtils';
import { getRedirectPath } from '../../utils/roleUtils';
import './LoginPage.css';

const LoginPage = () => {
  const [showPassword, setShowPassword] = useState(false);
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    name: '',
    userType: 'PATIENT' as 'PATIENT' | 'MEDECIN',
    speciality: '',
    licenseNumber: ''
  });
  const [error, setError] = useState('');
  
  const { login, register, isLoading } = useAuth();
  const navigate = useNavigate();

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      if (isLogin) {
        const user = await login({
          username: formData.email,
          password: formData.password
        });
        // Rediriger selon le rôle de l'utilisateur
        const redirectPath = getRedirectPath(user);
        navigate(redirectPath);
      } else {
        if (formData.password !== formData.confirmPassword) {
          setError('Les mots de passe ne correspondent pas');
          return;
        }

        const nameParts = formData.name.trim().split(' ');
        const firstName = nameParts[0] || 'Prénom';
        const lastName = nameParts.slice(1).join(' ') || 'Nom';
        
        // Créer un username basé sur le nom (ex: jean.dupont)
        const username = `${firstName.toLowerCase()}.${lastName.toLowerCase().replace(/\s+/g, '')}`;
        
        await register({
          username: username,
          email: formData.email,
          password: formData.password,
          firstName: firstName,
          lastName: lastName,
          userType: formData.userType,
          speciality: formData.userType === 'MEDECIN' ? formData.speciality : undefined,
          licenseNumber: formData.userType === 'MEDECIN' ? formData.licenseNumber : undefined
        });
        
        // Rediriger vers la page de confirmation avec les informations de l'utilisateur
        navigate('/confirmation-inscription', {
          state: {
            userType: formData.userType,
            email: formData.email,
            firstName: firstName
          }
        });
      }
    } catch (error) {
      const rawError = error instanceof Error ? error.message : 'Une erreur est survenue';
      setError(cleanErrorMessage(rawError));
    }
  };

  return (
    <div className="login-page">
      <div className="login-container">
        <div className="login-header">
          <Link to="/" className="login-logo">
            <Calendar className="login-logo-icon" />
            <span className="login-logo-text">RDV360</span>
          </Link>
          <h2 className="login-title">
            {isLogin ? 'Connectez-vous' : 'Créez votre compte'}
          </h2>
          <p className="login-subtitle">
            {isLogin 
              ? 'Accédez à votre espace de gestion' 
              : 'Commencez votre essai gratuit dès maintenant'
            }
          </p>
        </div>

        <div className="login-form-container">
          {error && (
            <div className="login-error">
              {error}
            </div>
          )}
          <form className="login-form" onSubmit={handleSubmit}>
            {!isLogin && (
              <div className="login-field">
                <label htmlFor="name" className="login-label">
                  Nom complet
                </label>
                <input
                  id="name"
                  name="name"
                  type="text"
                  required={!isLogin}
                  value={formData.name}
                  onChange={handleInputChange}
                  className="login-input"
                  placeholder="Votre nom complet"
                />
              </div>
            )}

            <div className="login-field">
              <label htmlFor="email" className="login-label">
                Adresse email
              </label>
              <div className="relative">
                <Mail className="login-input-icon-left" />
                <input
                  id="email"
                  name="email"
                  type="email"
                  required
                  value={formData.email}
                  onChange={handleInputChange}
                  className="login-input login-input-with-left-icon"
                  placeholder="votre@email.com"
                />
              </div>
            </div>

            <div className="login-field">
              <label htmlFor="password" className="login-label">
                Mot de passe
              </label>
              <div className="relative">
                <Lock className="login-input-icon-left" />
                <input
                  id="password"
                  name="password"
                  type={showPassword ? 'text' : 'password'}
                  required
                  value={formData.password}
                  onChange={handleInputChange}
                  className="login-input login-input-with-left-icon login-input-with-right-icon"
                  placeholder="••••••••"
                />
                <button
                  type="button"
                  className="login-input-icon-right"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? (
                    <EyeOff className="login-password-toggle" />
                  ) : (
                    <Eye className="login-password-toggle" />
                  )}
                </button>
              </div>
            </div>

            {!isLogin && (
              <div className="login-field">
                <label htmlFor="confirmPassword" className="login-label">
                  Confirmer le mot de passe
                </label>
                <div className="relative">
                  <Lock className="login-input-icon-left" />
                  <input
                    id="confirmPassword"
                    name="confirmPassword"
                    type="password"
                    required={!isLogin}
                    value={formData.confirmPassword}
                    onChange={handleInputChange}
                    className="login-input login-input-with-left-icon"
                    placeholder="••••••••"
                  />
                </div>
              </div>
            )}

            {!isLogin && (
              <div className="login-field">
                <label htmlFor="userType" className="login-label">
                  Je suis un(e)...
                </label>
                <select
                  id="userType"
                  name="userType"
                  value={formData.userType}
                  onChange={handleInputChange}
                  className="login-input"
                  required={!isLogin}
                >
                  <option value="PATIENT">👤 Patient</option>
                  <option value="MEDECIN">🏥 Médecin</option>
                </select>
              </div>
            )}

            {!isLogin && formData.userType === 'MEDECIN' && (
              <>
                <div className="login-field">
                  <label htmlFor="speciality" className="login-label">
                    Spécialité médicale
                  </label>
                  <select
                    id="speciality"
                    name="speciality"
                    value={formData.speciality}
                    onChange={handleInputChange}
                    className="login-input"
                    required={!isLogin && formData.userType === 'MEDECIN'}
                  >
                    <option value="">Sélectionnez votre spécialité</option>
                    <option value="medecine-generale">Médecine Générale</option>
                    <option value="cardiologie">Cardiologie</option>
                    <option value="orl">ORL</option>
                    <option value="pediatrie">Pédiatrie</option>
                    <option value="gynecologie">Gynécologie</option>
                    <option value="dermatologie">Dermatologie</option>
                    <option value="psychiatrie">Psychiatrie</option>
                    <option value="ophtalmologie">Ophtalmologie</option>
                    <option value="autre">Autre</option>
                  </select>
                </div>

                <div className="login-field">
                  <label htmlFor="licenseNumber" className="login-label">
                    Numéro d'ordre des médecins
                  </label>
                  <input
                    id="licenseNumber"
                    name="licenseNumber"
                    type="text"
                    value={formData.licenseNumber}
                    onChange={handleInputChange}
                    className="login-input"
                    placeholder="Ex: 123456789"
                    required={!isLogin && formData.userType === 'MEDECIN'}
                  />
                </div>

                <div className="bg-orange-50 border border-orange-200 rounded-lg p-4 mb-4">
                  <div className="flex items-start">
                    <div className="flex-shrink-0">
                      <svg className="w-5 h-5 text-orange-400 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
                      </svg>
                    </div>
                    <div className="ml-3">
                      <h3 className="text-sm font-medium text-orange-800">
                        Validation requise pour les médecins
                      </h3>
                      <p className="text-sm text-orange-700 mt-1">
                        Votre compte sera créé mais nécessitera une validation par un administrateur. 
                        Vous devrez fournir vos documents justificatifs (diplôme, ordre des médecins, etc.).
                      </p>
                    </div>
                  </div>
                </div>
              </>
            )}

            {isLogin && (
              <div className="login-remember-forgot">
                <label className="login-remember">
                  <input
                    type="checkbox"
                    className="login-checkbox"
                  />
                  <span className="login-checkbox-label">Se souvenir de moi</span>
                </label>
                <button type="button" className="login-forgot-password">
                  Mot de passe oublié ?
                </button>
              </div>
            )}

            <button
              type="submit"
              disabled={isLoading}
              className="login-submit-button"
            >
              {isLoading 
                ? (isLogin ? 'Connexion...' : 'Création...') 
                : (isLogin ? 'Se connecter' : 'Créer mon compte')
              }
            </button>
          </form>

          <div className="login-toggle-container">
            <p className="login-toggle-text">
              {isLogin ? "Pas encore de compte ?" : "Déjà un compte ?"}
              <button
                onClick={() => setIsLogin(!isLogin)}
                className="login-toggle-button"
              >
                {isLogin ? 'Créer un compte' : 'Se connecter'}
              </button>
            </p>
          </div>

          {!isLogin && (
            <div className="login-terms">
              En créant un compte, vous acceptez nos{' '}
              <a href="#" className="login-terms-link">conditions d'utilisation</a>
              {' '}et notre{' '}
              <a href="#" className="login-terms-link">politique de confidentialité</a>.
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default LoginPage;