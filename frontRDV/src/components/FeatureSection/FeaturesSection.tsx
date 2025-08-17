import React from 'react';
import { Calendar, Clock, Users, Smartphone, BarChart3, Shield } from 'lucide-react';
import './FeaturesSection.css';

const FeaturesSection = () => {
  const features = [
    {
      icon: Calendar,
      title: "Réservation en ligne",
      description: "Interface intuitive pour vos clients. Réservation 24h/24, 7j/7 sans contraintes.",
      color: "blue"
    },
    {
      icon: Clock,
      title: "Gestion des créneaux",
      description: "Optimisez votre planning automatiquement. Évitez les créneaux vides et maximisez votre chiffre d'affaires.",
      color: "orange"
    },
    {
      icon: Users,
      title: "Gestion clientèle",
      description: "Centralisez toutes les informations clients. Historique, préférences et suivi personnalisé.",
      color: "blue"
    },
    {
      icon: Smartphone,
      title: "Application mobile",
      description: "Accédez à votre planning partout. Notifications push et synchronisation en temps réel.",
      color: "orange"
    },
    {
      icon: BarChart3,
      title: "Statistiques avancées",
      description: "Analysez votre activité avec des rapports détaillés. KPIs et tendances en un clic.",
      color: "blue"
    },
    {
      icon: Shield,
      title: "Sécurité garantie",
      description: "Vos données sont protégées. Conformité RGPD et chiffrement de bout en bout.",
      color: "orange"
    }
  ];

  return (
    <section id="fonctionnalites" className="features-section">
      <div className="features-container">
        <div className="features-header">
          <h2 className="features-title">
            Tout ce dont vous avez besoin
          </h2>
          <p className="features-description">
            Une solution complète pour digitaliser votre activité et offrir une expérience exceptionnelle à vos clients.
          </p>
        </div>

        <div className="features-grid">
          {features.map((feature, index) => (
            <div
              key={index}
              className="feature-card group"
            >
              <div className={`feature-icon ${
                feature.color === 'blue' 
                  ? 'feature-icon-blue' 
                  : 'feature-icon-orange'
              }`}>
                <feature.icon className="feature-icon-element" />
              </div>
              <h3 className="feature-title">
                {feature.title}
              </h3>
              <p className="feature-description">
                {feature.description}
              </p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};

export default FeaturesSection;