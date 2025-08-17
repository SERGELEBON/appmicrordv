import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight, Calendar, Clock, Users } from 'lucide-react';
import './HeroSection.css';

const HeroSection = () => {
  return (
    <section className="hero-section">
      <div className="hero-overlay"></div>
      <div className="hero-container">
        <div className="hero-grid">
          <div className="hero-content">
            <div className="hero-text-content">
              <h1 className="hero-title">
                Simplifiez vos 
                <span className="hero-title-accent"> rendez-vous</span>
              </h1>
              <p className="hero-description">
                La plateforme complète pour gérer vos rendez-vous, optimiser votre temps 
                et améliorer l'expérience de vos clients.
              </p>
            </div>

            <div className="hero-buttons">
              <Link
                to="/ajouter-etablissement"
                className="hero-button-primary"
              >
                Commencer gratuitement
                <ArrowRight className="ml-2 h-5 w-5" />
              </Link>
              <button className="hero-button-secondary">
                Voir la démo
              </button>
            </div>

            <div className="hero-stats">
              <div className="hero-stat-item">
                <div className="hero-stat-icon">
                  <Calendar className="hero-stat-icon-element" />
                </div>
                <div className="hero-stat-number">10K+</div>
                <div className="hero-stat-label">Rendez-vous/jour</div>
              </div>
              <div className="hero-stat-item">
                <div className="hero-stat-icon">
                  <Users className="hero-stat-icon-element" />
                </div>
                <div className="hero-stat-number">2K+</div>
                <div className="hero-stat-label">Établissements</div>
              </div>
              <div className="hero-stat-item">
                <div className="hero-stat-icon">
                  <Clock className="hero-stat-icon-element" />
                </div>
                <div className="hero-stat-number">24/7</div>
                <div className="hero-stat-label">Disponibilité</div>
              </div>
            </div>
          </div>

          <div className="relative">
            <div className="hero-demo-card">
              <div className="hero-demo-content">
                <div className="hero-demo-header">
                  <h3 className="hero-demo-title">Réservation en ligne</h3>
                  <div className="hero-demo-status"></div>
                </div>
                <div className="hero-demo-appointments">
                  <div className="hero-demo-appointment hero-demo-appointment-blue">
                    <div className="hero-demo-appointment-icon hero-demo-appointment-icon-blue">
                      <Calendar className="hero-demo-appointment-icon-element" />
                    </div>
                    <div>
                      <div className="hero-demo-appointment-info">Dr. Martin</div>
                      <div className="hero-demo-appointment-time">Aujourd'hui 14:30</div>
                    </div>
                  </div>
                  <div className="hero-demo-appointment hero-demo-appointment-orange">
                    <div className="hero-demo-appointment-icon hero-demo-appointment-icon-orange">
                      <Users className="hero-demo-appointment-icon-element" />
                    </div>
                    <div>
                      <div className="hero-demo-appointment-info">Salon Beauté</div>
                      <div className="hero-demo-appointment-time">Demain 10:00</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default HeroSection;