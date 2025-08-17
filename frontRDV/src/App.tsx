import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar/Navbar.tsx';
import HomePage from './pages/Home/HomePage.tsx';
import LoginPage from './pages/Login/LoginPage.tsx';
import AddEstablishmentPage from './pages/FormAddEtabli/AddEstablishmentPage.tsx';
import AdminDashboard from './pages/Admin/AdminDashboard.tsx';
import DoctorDashboard from './pages/Doctor/DoctorDashboard.tsx';
import PatientDashboard from './pages/Patient/PatientDashboard.tsx';
import RegistrationConfirmationPage from './pages/RegistrationConfirmation/RegistrationConfirmationPage.tsx';
import ProfilePage from './pages/Profile/ProfilePage.tsx';
import Footer from './components/Footer/Footer.tsx';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-slate-50">
          <Navbar />
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/connexion" element={<LoginPage />} />
            <Route path="/confirmation-inscription" element={<RegistrationConfirmationPage />} />
            <Route path="/profil" element={<ProfilePage />} />
            <Route path="/ajouter-etablissement" element={<AddEstablishmentPage />} />
            <Route path="/admin" element={<AdminDashboard />} />
            <Route path="/dashboard" element={<DoctorDashboard />} />
            <Route path="/patient" element={<PatientDashboard />} />
          </Routes>
          <Footer />
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;