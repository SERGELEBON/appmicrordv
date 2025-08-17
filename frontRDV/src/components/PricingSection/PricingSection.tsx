import React from 'react';
import { Check, Star } from 'lucide-react';
import { Link } from 'react-router-dom';
import './PricingSection.css';

const PricingSection = () => {
  const plans = [
    {
      name: "Starter",
      price: "0",
      period: "Gratuit",
      description: "Parfait pour commencer",
      features: [
        "Jusqu'à 50 rendez-vous/mois",
        "1 établissement",
        "Réservation en ligne",
        "Support email",
        "Interface web"
      ],
      buttonText: "Commencer gratuitement",
      buttonStyle: "border-2 border-blue-600 text-blue-600 hover:bg-blue-50"
    },
    {
      name: "Professional",
      price: "15 000",
      period: "/mois",
      description: "Pour les professionnels",
      features: [
        "Rendez-vous illimités",
        "3 établissements",
        "Application mobile",
        "Statistiques avancées",
        "Support prioritaire",
        "Rappels automatiques"
      ],
      buttonText: "Essayer 14 jours gratuits",
      buttonStyle: "bg-blue-600 text-white hover:bg-blue-700",
      popular: true
    },
    {
      name: "Enterprise",
      price: "50 000",
      period: "/mois",
      description: "Pour les grandes structures",
      features: [
        "Tout du plan Professional",
        "Établissements illimités",
        "API personnalisée",
        "Intégrations avancées",
        "Support dédié 24/7",
        "Formation incluse"
      ],
      buttonText: "Contactez-nous",
      buttonStyle: "bg-orange-500 text-white hover:bg-orange-600"
    }
  ];

  return (
    <section id="tarifs" className="pricing-section">
      <div className="pricing-container">
        <div className="pricing-header">
          <h2 className="pricing-title">
            Tarifs transparents
          </h2>
          <p className="pricing-description">
            Choisissez le plan qui correspond à vos besoins. Changez à tout moment, sans engagement.
          </p>
        </div>

        <div className="pricing-grid">
          {plans.map((plan, index) => (
            <div
              key={index}
              className={`pricing-card ${
                plan.popular ? 'pricing-card-popular' : ''
              }`}
            >
              {plan.popular && (
                <div className="pricing-popular-badge">
                  <div className="pricing-popular-badge-content">
                    <Star className="h-4 w-4" />
                    <span>Populaire</span>
                  </div>
                </div>
              )}

              <div className="pricing-card-header">
                <h3 className="pricing-plan-name">{plan.name}</h3>
                <div className="pricing-price-container">
                  <span className="pricing-price">{plan.price} FCFA</span>
                  <span className="pricing-period">{plan.period}</span>
                </div>
                <p className="pricing-plan-description">{plan.description}</p>
              </div>

              <ul className="pricing-features">
                {plan.features.map((feature, featureIndex) => (
                  <li key={featureIndex} className="pricing-feature">
                    <Check className="pricing-feature-icon" />
                    <span className="pricing-feature-text">{feature}</span>
                  </li>
                ))}
              </ul>

              <Link
                to="/ajouter-etablissement"
                className={`pricing-button ${
                  plan.name === 'Starter' ? 'pricing-button-starter' :
                  plan.name === 'Professional' ? 'pricing-button-professional' :
                  'pricing-button-enterprise'
                }`}
              >
                {plan.buttonText}
              </Link>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};

export default PricingSection;