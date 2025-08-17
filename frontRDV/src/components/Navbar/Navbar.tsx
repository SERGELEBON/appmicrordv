import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Calendar, Menu, X, User, Building, LogOut, UserCircle } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { isAdmin, isMedecin, isPatient } from '../../utils/roleUtils';
import './Navbar.css';

const Navbar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const { isAuthenticated, user, logout } = useAuth();

  // Fonction pour afficher un nom convivial
  const getDisplayName = () => {
    if (!user) return '';
    
    // Si c'est l'admin, afficher le username
    if (user.username === 'admin') {
      return user.username;
    }
    
    // Pour les autres, essayer d'extraire un nom du username ou email
    if (user.username && user.username.includes('@')) {
      // Si username est un email, extraire la partie avant @
      return user.username.split('@')[0].replace(/[._]/g, ' ');
    }
    
    // Si username contient des points, les remplacer par des espaces
    return user.username.replace(/[._]/g, ' ');
  };

  // Fonction pour obtenir le lien et nom du tableau de bord selon le rôle
  const getDashboardInfo = () => {
    if (isAdmin(user)) {
      return { path: '/admin', name: 'Administration' };
    }
    if (isMedecin(user)) {
      return { path: '/dashboard', name: 'Tableau de bord' };
    }
    if (isPatient(user)) {
      return { path: '/patient', name: 'Mes rendez-vous' };
    }
    return null;
  };

  const isActive = (path: string) => location.pathname === path;

  const handleLogout = async () => {
    try {
      await logout();
      setIsMenuOpen(false);
      // Rediriger vers la page d'accueil après la déconnexion
      navigate('/');
    } catch (error) {
      console.error('Logout error:', error);
    }
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-content">
          <Link to="/" className="navbar-logo">
            <Calendar className="navbar-logo-icon" />
            <span className="navbar-logo-text">RDV Chez Doc</span>
          </Link>

          {/* Desktop Navigation */}
          <div className="navbar-desktop">
            <Link
              to="/"
              className={`navbar-link ${
                isActive('/') 
                  ? 'navbar-link-active' 
                  : 'navbar-link-inactive'
              }`}
            >
              Accueil
            </Link>
            
            {/* Liens pour utilisateurs non connectés */}
            {!isAuthenticated && (
              <>
                <a
                  href="#fonctionnalites"
                  className="navbar-link navbar-link-inactive"
                >
                  Fonctionnalités
                </a>
                <a
                  href="#tarifs"
                  className="navbar-link navbar-link-inactive"
                >
                  Tarifs
                </a>
              </>
            )}
            
            {/* Lien tableau de bord pour utilisateurs connectés */}
            {isAuthenticated && getDashboardInfo() && (
              <Link
                to={getDashboardInfo()!.path}
                className={`navbar-link ${
                  isActive(getDashboardInfo()!.path) 
                    ? 'navbar-link-active' 
                    : 'navbar-link-inactive'
                }`}
              >
                {getDashboardInfo()!.name}
              </Link>
            )}
            {!isAuthenticated ? (
              <>
                <Link
                  to="/connexion"
                  className={`navbar-button-login ${
                    isActive('/connexion')
                      ? 'navbar-button-login-active'
                      : 'navbar-button-login-inactive'
                  }`}
                >
                  <User className="h-4 w-4" />
                  <span>Se connecter</span>
                </Link>
                <Link
                  to="/ajouter-etablissement"
                  className={`navbar-button-add ${
                    isActive('/ajouter-etablissement')
                      ? 'navbar-button-add-active'
                      : 'navbar-button-add-inactive'
                  }`}
                >
                  <Building className="h-4 w-4" />
                  <span>Ajouter établissement</span>
                </Link>
              </>
            ) : (
              <div className="navbar-user-menu">
                <Link to="/profil" className="navbar-user-name">
                  <UserCircle className="h-4 w-4" />
                  Bonjour, {getDisplayName()}
                </Link>
                <button
                  onClick={handleLogout}
                  className="navbar-button-logout"
                >
                  <LogOut className="h-4 w-4" />
                  <span>Déconnexion</span>
                </button>
              </div>
            )}
            {/* Ajouter établissement - uniquement pour les admins */}
            {isAdmin(user) && (
              <Link
                to="/ajouter-etablissement"
                className={`navbar-button-add ${
                  isActive('/ajouter-etablissement')
                    ? 'navbar-button-add-active'
                    : 'navbar-button-add-inactive'
                }`}
              >
                <Building className="h-4 w-4" />
                <span>Ajouter établissement</span>
              </Link>
            )}
          </div>

          {/* Mobile menu button */}
          <div className="navbar-mobile-button">
            <button
              onClick={() => setIsMenuOpen(!isMenuOpen)}
            >
              {isMenuOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
            </button>
          </div>
        </div>

        {/* Mobile Navigation */}
        {isMenuOpen && (
          <div className="navbar-mobile-menu">
              <Link
                to="/"
                className={`navbar-mobile-link ${
                  isActive('/') 
                    ? 'navbar-mobile-link-active' 
                    : 'navbar-mobile-link-inactive'
                }`}
                onClick={() => setIsMenuOpen(false)}
              >
                Accueil
              </Link>
              
              {/* Liens pour utilisateurs non connectés */}
              {!isAuthenticated && (
                <>
                  <a
                    href="#fonctionnalites"
                    className="navbar-mobile-link navbar-mobile-link-inactive"
                    onClick={() => setIsMenuOpen(false)}
                  >
                    Fonctionnalités
                  </a>
                  <a
                    href="#tarifs"
                    className="navbar-mobile-link navbar-mobile-link-inactive"
                    onClick={() => setIsMenuOpen(false)}
                  >
                    Tarifs
                  </a>
                </>
              )}
              
              {/* Lien tableau de bord pour utilisateurs connectés */}
              {isAuthenticated && getDashboardInfo() && (
                <Link
                  to={getDashboardInfo()!.path}
                  className={`navbar-mobile-link ${
                    isActive(getDashboardInfo()!.path) 
                      ? 'navbar-mobile-link-active' 
                      : 'navbar-mobile-link-inactive'
                  }`}
                  onClick={() => setIsMenuOpen(false)}
                >
                  {getDashboardInfo()!.name}
                </Link>
              )}
              {!isAuthenticated ? (
                <>
                  <Link
                    to="/connexion"
                    className="navbar-mobile-button-login"
                    onClick={() => setIsMenuOpen(false)}
                  >
                    <User className="h-5 w-5" />
                    <span>Se connecter</span>
                  </Link>
                  <Link
                    to="/ajouter-etablissement"
                    className="navbar-mobile-button-add"
                    onClick={() => setIsMenuOpen(false)}
                  >
                    <Building className="h-5 w-5" />
                    <span>Ajouter établissement</span>
                  </Link>
                </>
              ) : (
                <div className="navbar-mobile-user">
                  <Link 
                    to="/profil" 
                    className="navbar-mobile-user-name"
                    onClick={() => setIsMenuOpen(false)}
                  >
                    <UserCircle className="h-5 w-5" />
                    Bonjour, {getDisplayName()}
                  </Link>
                  <button
                    onClick={handleLogout}
                    className="navbar-mobile-button-logout"
                  >
                    <LogOut className="h-5 w-5" />
                    <span>Déconnexion</span>
                  </button>
                </div>
              )}
              {/* Ajouter établissement - uniquement pour les admins */}
              {isAdmin(user) && (
                <Link
                  to="/ajouter-etablissement"
                  className="navbar-mobile-button-add"
                  onClick={() => setIsMenuOpen(false)}
                >
                  <Building className="h-5 w-5" />
                  <span>Ajouter établissement</span>
                </Link>
              )}
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;