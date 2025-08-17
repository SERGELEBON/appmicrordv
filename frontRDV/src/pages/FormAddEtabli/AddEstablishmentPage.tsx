import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Calendar, Building, MapPin, Phone, Clock, Users, Upload, Camera } from 'lucide-react';
import './AddEstablishmentPage.css';

const AddEstablishmentPage = () => {
  const [currentStep, setCurrentStep] = useState(1);
  const [formData, setFormData] = useState({
    name: '',
    category: '',
    description: '',
    address: '',
    city: '',
    postalCode: '',
    phone: '',
    email: '',
    website: '',
    openingHours: {
      monday: { open: '09:00', close: '18:00', closed: false },
      tuesday: { open: '09:00', close: '18:00', closed: false },
      wednesday: { open: '09:00', close: '18:00', closed: false },
      thursday: { open: '09:00', close: '18:00', closed: false },
      friday: { open: '09:00', close: '18:00', closed: false },
      saturday: { open: '09:00', close: '17:00', closed: false },
      sunday: { open: '10:00', close: '16:00', closed: true }
    }
  });

  const categories = [
    'Santé & Médecine',
    'Beauté & Bien-être',
    'Services automobiles',
    'Éducation & Formation',
    'Services juridiques',
    'Restauration',
    'Services à domicile',
    'Sport & Fitness',
    'Autre'
  ];

  const days = [
    { key: 'monday', label: 'Lundi' },
    { key: 'tuesday', label: 'Mardi' },
    { key: 'wednesday', label: 'Mercredi' },
    { key: 'thursday', label: 'Jeudi' },
    { key: 'friday', label: 'Vendredi' },
    { key: 'saturday', label: 'Samedi' },
    { key: 'sunday', label: 'Dimanche' }
  ];

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleHoursChange = (day: string, field: string, value: string | boolean) => {
    setFormData({
      ...formData,
      openingHours: {
        ...formData.openingHours,
        [day]: {
          ...formData.openingHours[day as keyof typeof formData.openingHours],
          [field]: value
        }
      }
    });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    console.log('Establishment data:', formData);
    // Handle form submission
  };

  const nextStep = () => {
    if (currentStep < 3) setCurrentStep(currentStep + 1);
  };

  const prevStep = () => {
    if (currentStep > 1) setCurrentStep(currentStep - 1);
  };

  return (
    <div className="add-establishment-page">
      <div className="add-establishment-container">
        {/* Header */}
        <div className="add-establishment-header">
          <Link to="/" className="add-establishment-logo">
            <Calendar className="add-establishment-logo-icon" />
            <span className="add-establishment-logo-text">RDV360</span>
          </Link>
          <h1 className="add-establishment-title">
            Ajoutez votre établissement
          </h1>
          <p className="add-establishment-subtitle">
            Créez votre profil en quelques étapes simples et commencez à recevoir des réservations
          </p>
        </div>

        {/* Progress Bar */}
        <div className="progress-bar-container">
          <div className="progress-bar">
            {[1, 2, 3].map((step) => (
              <div key={step} className="progress-step">
                <div className={`progress-step-circle ${
                  step <= currentStep 
                    ? 'progress-step-active' 
                    : 'progress-step-inactive'
                }`}>
                  {step}
                </div>
                {step < 3 && (
                  <div className={`progress-line ${
                    step < currentStep ? 'progress-line-active' : 'progress-line-inactive'
                  }`}></div>
                )}
              </div>
            ))}
          </div>
          <div className="progress-labels">
            <span>Informations générales</span>
            <span>Horaires & Services</span>
            <span>Finalisation</span>
          </div>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-container">
            {/* Step 1: Basic Information */}
            {currentStep === 1 && (
              <div className="form-step">
                <div className="step-header">
                  <Building className="step-icon" />
                  <h2 className="step-title">Informations générales</h2>
                </div>

                <div className="form-grid form-grid-2">
                  <div>
                    <label className="form-label">
                      Nom de l'établissement *
                    </label>
                    <input
                      type="text"
                      name="name"
                      required
                      value={formData.name}
                      onChange={handleInputChange}
                      className="form-input"
                      placeholder="Cabinet Dr. Martin"
                    />
                  </div>

                  <div>
                    <label className="form-label">
                      Catégorie *
                    </label>
                    <select
                      name="category"
                      required
                      value={formData.category}
                      onChange={handleInputChange}
                      className="form-select"
                    >
                      <option value="">Sélectionnez une catégorie</option>
                      {categories.map((category) => (
                        <option key={category} value={category}>{category}</option>
                      ))}
                    </select>
                  </div>

                  <div className="form-grid-full">
                    <label className="form-label">
                      Description
                    </label>
                    <textarea
                      name="description"
                      rows={4}
                      value={formData.description}
                      onChange={handleInputChange}
                      className="form-textarea"
                      placeholder="Décrivez votre établissement, vos services..."
                    />
                  </div>

                  <div>
                    <label className="form-label">
                      Adresse *
                    </label>
                    <div className="relative">
                      <MapPin className="form-input-icon" />
                      <input
                        type="text"
                        name="address"
                        required
                        value={formData.address}
                        onChange={handleInputChange}
                        className="form-input form-input-with-left-icon"
                        placeholder="123 rue de la République"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="form-label">
                      Ville *
                    </label>
                    <input
                      type="text"
                      name="city"
                      required
                      value={formData.city}
                      onChange={handleInputChange}
                      className="form-input"
                      placeholder="Paris"
                    />
                  </div>

                  <div>
                    <label className="form-label">
                      Code postal *
                    </label>
                    <input
                      type="text"
                      name="postalCode"
                      required
                      value={formData.postalCode}
                      onChange={handleInputChange}
                      className="form-input"
                      placeholder="75001"
                    />
                  </div>

                  <div>
                    <label className="form-label">
                      Téléphone *
                    </label>
                    <div className="relative">
                      <Phone className="form-input-icon" />
                      <input
                        type="tel"
                        name="phone"
                        required
                        value={formData.phone}
                        onChange={handleInputChange}
                        className="form-input form-input-with-left-icon"
                        placeholder="01 23 45 67 89"
                      />
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Step 2: Hours & Services */}
            {currentStep === 2 && (
              <div className="form-step">
                <div className="step-header">
                  <Clock className="step-icon" />
                  <h2 className="step-title">Horaires d'ouverture</h2>
                </div>

                <div className="hours-container">
                  {days.map((day) => (
                    <div key={day.key} className="hours-row">
                      <div className="hours-day">
                        <span>{day.label}</span>
                      </div>
                      
                      <label className="hours-closed-checkbox">
                        <input
                          type="checkbox"
                          checked={formData.openingHours[day.key as keyof typeof formData.openingHours].closed}
                          onChange={(e) => handleHoursChange(day.key, 'closed', e.target.checked)}
                          className="hours-checkbox"
                        />
                        <span className="hours-checkbox-label">Fermé</span>
                      </label>

                      {!formData.openingHours[day.key as keyof typeof formData.openingHours].closed && (
                        <div className="hours-time-inputs">
                          <input
                            type="time"
                            value={formData.openingHours[day.key as keyof typeof formData.openingHours].open}
                            onChange={(e) => handleHoursChange(day.key, 'open', e.target.value)}
                            className="hours-time-input"
                          />
                          <span className="hours-separator">à</span>
                          <input
                            type="time"
                            value={formData.openingHours[day.key as keyof typeof formData.openingHours].close}
                            onChange={(e) => handleHoursChange(day.key, 'close', e.target.value)}
                            className="hours-time-input"
                          />
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Step 3: Finalization */}
            {currentStep === 3 && (
              <div className="form-step">
                <div className="step-header">
                  <Users className="step-icon" />
                  <h2 className="step-title">Finalisation</h2>
                </div>

                <div className="summary-card summary-card-blue">
                  <h3 className="summary-title summary-title-blue">Récapitulatif</h3>
                  <div className="summary-content summary-text-blue">
                    <p><strong>Établissement:</strong> {formData.name || 'Non renseigné'}</p>
                    <p><strong>Catégorie:</strong> {formData.category || 'Non renseignée'}</p>
                    <p><strong>Adresse:</strong> {formData.address || 'Non renseignée'}</p>
                    <p><strong>Téléphone:</strong> {formData.phone || 'Non renseigné'}</p>
                  </div>
                </div>

                <div className="summary-card summary-card-green">
                  <h3 className="summary-title summary-title-green">Prochaines étapes</h3>
                  <ul className="summary-list summary-text-green">
                    <li>✓ Configuration de votre planning</li>
                    <li>✓ Création de vos services</li>
                    <li>✓ Personnalisation de votre page de réservation</li>
                    <li>✓ Formation et support inclus</li>
                  </ul>
                </div>
              </div>
            )}

            {/* Navigation Buttons */}
            <div className="form-navigation">
              {currentStep > 1 && (
                <button
                  type="button"
                  onClick={prevStep}
                  className="nav-button nav-button-secondary"
                >
                  Précédent
                </button>
              )}
              
              <div className="ml-auto">
                {currentStep < 3 ? (
                  <button
                    type="button"
                    onClick={nextStep}
                    className="nav-button nav-button-primary"
                  >
                    Suivant
                  </button>
                ) : (
                  <button
                    type="submit"
                    className="nav-button nav-button-success"
                  >
                    Créer mon établissement
                  </button>
                )}
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddEstablishmentPage;