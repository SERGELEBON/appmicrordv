import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Calendar, CheckCircle, Mail, Clock } from 'lucide-react';
import './RegistrationConfirmationPage.css';

interface LocationState {
  userType?: 'PATIENT' | 'MEDECIN';
  email?: string;
  firstName?: string;
}

const RegistrationConfirmationPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const state = location.state as LocationState;

  // Si pas d'état, rediriger vers la page de connexion
  React.useEffect(() => {
    if (!state?.userType) {
      navigate('/login');
    }
  }, [state, navigate]);

  const isDoctor = state?.userType === 'MEDECIN';
  const userFirstName = state?.firstName || 'Utilisateur';
  const userEmail = state?.email || '';

  return (
    <div className="registration-confirmation-page">
      <div className="confirmation-container">
        <div className="confirmation-header">
          <Link to="/" className="confirmation-logo">
            <Calendar className="confirmation-logo-icon" />
            <span className="confirmation-logo-text">RDV360</span>
          </Link>
        </div>

        <div className="confirmation-content">
          <div className="confirmation-icon">
            <CheckCircle className="success-icon" />
          </div>

          <h1 className="confirmation-title">
            Inscription réussie !
          </h1>

          <p className="confirmation-greeting">
            Merci {userFirstName}, votre compte a été créé avec succès.
          </p>

          {isDoctor ? (
            <div className="doctor-validation-info">
              <div className="info-card">
                <Clock className="info-icon" />
                <h3>Validation en cours</h3>
                <p>
                  Votre demande de validation est en cours d'examen par nos administrateurs. 
                  Cette étape est nécessaire pour vérifier vos qualifications professionnelles.
                </p>
              </div>

              <div className="info-card">
                <Mail className="info-icon" />
                <h3>Email de confirmation</h3>
                <p>
                  Vous recevrez un email de confirmation à l'adresse <strong>{userEmail}</strong> 
                  sous 24-48h avec les instructions pour finaliser votre compte.
                </p>
              </div>

              <div className="next-steps">
                <h3>Prochaines étapes :</h3>
                <ul>
                  <li>Vérifiez votre boîte mail régulièrement</li>
                  <li>Préparez vos documents justificatifs (diplôme, ordre des médecins)</li>
                  <li>Attendez l'email de validation de notre équipe</li>
                </ul>
              </div>
            </div>
          ) : (
            <div className="patient-confirmation-info">
              <div className="info-card">
                <Mail className="info-icon" />
                <h3>Email de bienvenue</h3>
                <p>
                  Un email de bienvenue va être envoyé à l'adresse <strong>{userEmail}</strong> 
                  avec toutes les informations pour commencer à utiliser RDV360.
                </p>
              </div>

              <div className="info-card">
                <CheckCircle className="info-icon" />
                <h3>Accès immédiat</h3>
                <p>
                  Votre compte patient est activé ! Vous pouvez dès maintenant prendre 
                  rendez-vous avec nos médecins partenaires.
                </p>
              </div>
            </div>
          )}

          <div className="confirmation-actions">
            {isDoctor ? (
              <div className="doctor-actions">
                <Link to="/login" className="btn-primary">
                  Retour à la connexion
                </Link>
                <Link to="/" className="btn-secondary">
                  Retour à l'accueil
                </Link>
              </div>
            ) : (
              <div className="patient-actions">
                <Link to="/login" className="btn-primary">
                  Se connecter maintenant
                </Link>
                <Link to="/" className="btn-secondary">
                  Découvrir RDV360
                </Link>
              </div>
            )}
          </div>

          <div className="support-info">
            <p>
              Besoin d'aide ? Contactez notre support à{' '}
              <a href="mailto:support@rdv360.com">support@rdv360.com</a>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegistrationConfirmationPage;