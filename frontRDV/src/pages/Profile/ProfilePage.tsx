import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Calendar, User, Mail, Shield, Calendar as CalendarIcon, Edit, Save, X } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { isAdmin, isMedecin, isPatient } from '../../utils/roleUtils';
import './ProfilePage.css';

const ProfilePage = () => {
  const { user } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [editedUser, setEditedUser] = useState({
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
    email: user?.email || ''
  });

  if (!user) {
    return (
      <div className="profile-redirect">
        <div className="profile-redirect-content">
          <h2>Accès non autorisé</h2>
          <p>Vous devez être connecté pour accéder à votre profil.</p>
          <Link to="/connexion" className="btn-primary">
            Se connecter
          </Link>
        </div>
      </div>
    );
  }

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleCancel = () => {
    setIsEditing(false);
    setEditedUser({
      firstName: user?.firstName || '',
      lastName: user?.lastName || '',
      email: user?.email || ''
    });
  };

  const handleSave = () => {
    // TODO: Implémenter la sauvegarde
    console.log('Sauvegarder les modifications:', editedUser);
    setIsEditing(false);
  };

  const handleInputChange = (field: string, value: string) => {
    setEditedUser(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const getRoleDisplayName = () => {
    if (isAdmin(user)) return 'Administrateur';
    if (isMedecin(user)) return 'Médecin';
    if (isPatient(user)) return 'Patient';
    return 'Utilisateur';
  };

  const getRoleBadgeClass = () => {
    if (isAdmin(user)) return 'role-badge role-admin';
    if (isMedecin(user)) return 'role-badge role-medecin';
    if (isPatient(user)) return 'role-badge role-patient';
    return 'role-badge';
  };

  return (
    <div className="profile-page">
      <div className="profile-container">
        <div className="profile-header">
          <Link to="/" className="profile-logo">
            <Calendar className="profile-logo-icon" />
            <span className="profile-logo-text">RDV360</span>
          </Link>
          <nav className="profile-breadcrumb">
            <Link to="/">Accueil</Link>
            <span>/</span>
            <span>Mon Profil</span>
          </nav>
        </div>

        <div className="profile-content">
          <div className="profile-card">
            <div className="profile-card-header">
              <div className="profile-avatar">
                <User className="profile-avatar-icon" />
              </div>
              <div className="profile-title">
                <h1>Mon Profil</h1>
                <div className={getRoleBadgeClass()}>
                  <Shield className="role-icon" />
                  {getRoleDisplayName()}
                </div>
              </div>
              <div className="profile-actions">
                {!isEditing ? (
                  <button onClick={handleEdit} className="btn-edit">
                    <Edit className="btn-icon" />
                    Modifier
                  </button>
                ) : (
                  <div className="edit-actions">
                    <button onClick={handleSave} className="btn-save">
                      <Save className="btn-icon" />
                      Sauvegarder
                    </button>
                    <button onClick={handleCancel} className="btn-cancel">
                      <X className="btn-icon" />
                      Annuler
                    </button>
                  </div>
                )}
              </div>
            </div>

            <div className="profile-card-content">
              <div className="profile-section">
                <h3>Informations personnelles</h3>
                <div className="profile-grid">
                  <div className="profile-field">
                    <label>Nom d'utilisateur</label>
                    <div className="field-content">
                      <User className="field-icon" />
                      <span>{user.username}</span>
                    </div>
                  </div>

                  <div className="profile-field">
                    <label>Adresse email</label>
                    {isEditing ? (
                      <div className="field-content">
                        <Mail className="field-icon" />
                        <input
                          type="email"
                          value={editedUser.email}
                          onChange={(e) => handleInputChange('email', e.target.value)}
                          className="field-input"
                        />
                      </div>
                    ) : (
                      <div className="field-content">
                        <Mail className="field-icon" />
                        <span>{user.email}</span>
                      </div>
                    )}
                  </div>

                  <div className="profile-field">
                    <label>Prénom</label>
                    {isEditing ? (
                      <div className="field-content">
                        <User className="field-icon" />
                        <input
                          type="text"
                          value={editedUser.firstName}
                          onChange={(e) => handleInputChange('firstName', e.target.value)}
                          className="field-input"
                          placeholder="Votre prénom"
                        />
                      </div>
                    ) : (
                      <div className="field-content">
                        <User className="field-icon" />
                        <span>{user.firstName || 'Non renseigné'}</span>
                      </div>
                    )}
                  </div>

                  <div className="profile-field">
                    <label>Nom de famille</label>
                    {isEditing ? (
                      <div className="field-content">
                        <User className="field-icon" />
                        <input
                          type="text"
                          value={editedUser.lastName}
                          onChange={(e) => handleInputChange('lastName', e.target.value)}
                          className="field-input"
                          placeholder="Votre nom de famille"
                        />
                      </div>
                    ) : (
                      <div className="field-content">
                        <User className="field-icon" />
                        <span>{user.lastName || 'Non renseigné'}</span>
                      </div>
                    )}
                  </div>

                  <div className="profile-field">
                    <label>Identifiant</label>
                    <div className="field-content">
                      <span className="field-icon">#{user.id}</span>
                      <span>ID: {user.id}</span>
                    </div>
                  </div>

                  <div className="profile-field">
                    <label>Statut du compte</label>
                    <div className="field-content">
                      <div className={`status-badge ${user.isEnabled ? 'status-active' : 'status-inactive'}`}>
                        {user.isEnabled ? '✅ Actif' : '❌ Inactif'}
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div className="profile-section">
                <h3>Informations du compte</h3>
                <div className="profile-grid">
                  <div className="profile-field">
                    <label>Date de création</label>
                    <div className="field-content">
                      <CalendarIcon className="field-icon" />
                      <span>{new Date(user.createdAt).toLocaleDateString('fr-FR')}</span>
                    </div>
                  </div>

                  <div className="profile-field">
                    <label>Dernière modification</label>
                    <div className="field-content">
                      <CalendarIcon className="field-icon" />
                      <span>{new Date(user.updatedAt).toLocaleDateString('fr-FR')}</span>
                    </div>
                  </div>

                  <div className="profile-field">
                    <label>Rôles</label>
                    <div className="field-content">
                      <div className="roles-list">
                        {user.roles.map((role, index) => (
                          <span key={index} className={`role-tag role-${role.toLowerCase()}`}>
                            {role}
                          </span>
                        ))}
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              {isMedecin(user) && (
                <div className="profile-section">
                  <h3>Informations professionnelles</h3>
                  <div className="profile-grid">
                    <div className="profile-field">
                      <label>Spécialité</label>
                      <div className="field-content">
                        <span>Non renseignée</span>
                      </div>
                    </div>
                    <div className="profile-field">
                      <label>Numéro d'ordre</label>
                      <div className="field-content">
                        <span>Non renseigné</span>
                      </div>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>

          <div className="profile-actions-bottom">
            <Link to="/" className="btn-secondary">
              Retour à l'accueil
            </Link>
            {isAdmin(user) && (
              <Link to="/admin" className="btn-primary">
                Administration
              </Link>
            )}
            {isMedecin(user) && (
              <Link to="/dashboard" className="btn-primary">
                Tableau de bord
              </Link>
            )}
            {isPatient(user) && (
              <Link to="/patient" className="btn-primary">
                Mes rendez-vous
              </Link>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;