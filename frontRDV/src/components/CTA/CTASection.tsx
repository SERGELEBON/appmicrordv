import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight, Sparkles } from 'lucide-react';
import './CTASection.css';

const CTASection = () => {
  return (
    <section className="cta-section">
      <div className="cta-container">
        <div className="cta-content">
          <div className="cta-badge">
            <Sparkles className="cta-badge-icon" />
            <span>Nouveau : Intégration WhatsApp disponible</span>
          </div>
          
          <h2 className="cta-title">
            Prêt à transformer votre activité ?
          </h2>
          
          <p className="cta-description">
            Rejoignez plus de 2000 établissements qui ont déjà choisi RDV360 
            pour optimiser leur gestion des rendez-vous.
          </p>
          
          <div className="cta-buttons">
            <Link
              to="/ajouter-etablissement"
              className="cta-button-primary"
            >
              Démarrer maintenant
              <ArrowRight className="ml-2 h-5 w-5" />
            </Link>
            
            <button className="cta-button-secondary">
              Planifier une démo
            </button>
          </div>
          
          <div className="cta-features">
            ✓ Essai gratuit 14 jours • ✓ Sans engagement • ✓ Support inclus
          </div>
        </div>
      </div>
    </section>
  );
};

export default CTASection;