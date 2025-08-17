import React from 'react';
import { Calendar, Facebook, Twitter, Instagram, Linkedin, Mail, Phone, MapPin } from 'lucide-react';
import { Link } from 'react-router-dom';
import './Footer.css';

const Footer = () => {
  return (
    <footer className="footer">
      <div className="footer-container">
        <div className="footer-grid">
          {/* Logo & Description */}
          <div className="footer-brand">
            <div className="footer-logo">
              <Calendar className="footer-logo-icon" />
              <span className="footer-logo-text">RDV Chez Doc</span>
            </div>
            <p className="footer-description">
              La solution complète pour digitaliser votre activité et optimiser la gestion de vos rendez-vous.
            </p>
            <div className="footer-social">
              <a href="#" className="footer-social-link footer-social-link-facebook">
                <Facebook className="footer-social-icon" />
              </a>
              <a href="#" className="footer-social-link footer-social-link-twitter">
                <Twitter className="footer-social-icon" />
              </a>
              <a href="#" className="footer-social-link footer-social-link-instagram">
                <Instagram className="footer-social-icon" />
              </a>
              <a href="#" className="footer-social-link footer-social-link-linkedin">
                <Linkedin className="footer-social-icon" />
              </a>
            </div>
          </div>

          {/* Product */}
          <div className="footer-section">
            <h3 className="footer-section-title">Produit</h3>
            <ul className="footer-section-list">
              <li><a href="#fonctionnalites" className="footer-section-link">Fonctionnalités</a></li>
              <li><a href="#tarifs" className="footer-section-link">Tarifs</a></li>
              <li><a href="#" className="footer-section-link">Intégrations</a></li>
              <li><a href="#" className="footer-section-link">API</a></li>
              <li><a href="#" className="footer-section-link">Sécurité</a></li>
            </ul>
          </div>

          {/* Support */}
          <div className="footer-section">
            <h3 className="footer-section-title">Support</h3>
            <ul className="footer-section-list">
              <li><a href="#" className="footer-section-link">Centre d'aide</a></li>
              <li><a href="#" className="footer-section-link">Documentation</a></li>
              <li><a href="#" className="footer-section-link">Formation</a></li>
              <li><a href="#" className="footer-section-link">Status</a></li>
              <li><Link to="/connexion" className="footer-section-link">Contact</Link></li>
            </ul>
          </div>

          {/* Contact */}
          <div className="footer-contact">
            <h3 className="footer-section-title">Contact</h3>
            <div className="footer-contact-list">
              <div className="footer-contact-item">
                <Mail className="footer-contact-icon-blue" />
                <span className="footer-contact-text">contact@rdv360.com</span>
              </div>
              <div className="footer-contact-item">
                <Phone className="footer-contact-icon-orange" />
                <span className="footer-contact-text">01 03 08 10 65</span>
              </div>
              <div className="footer-contact-item">
                <MapPin className="footer-contact-icon-blue" />
                <span className="footer-contact-text">Abidjan, Cote d'Ivoire</span>
              </div>
            </div>
          </div>
        </div>

        <div className="footer-bottom">
          <div className="footer-bottom-content">
            <p className="footer-copyright">
              © 2025 RDV Chez Doc. Tous droits réservés.
            </p>
            <div className="footer-legal">
              <a href="#" className="footer-legal-link">
                Conditions d'utilisation
              </a>
              <a href="#" className="footer-legal-link">
                Politique de confidentialité
              </a>
              <a href="#" className="footer-legal-link">
                Cookies
              </a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;