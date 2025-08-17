import React from 'react';
import HeroSection from '../../components/HeroSection/HeroSection.tsx';
import FeaturesSection from '../../components/FeatureSection/FeaturesSection.tsx';
import PricingSection from '../../components/PricingSection/PricingSection.tsx';
import CTASection from '../../components/CTA/CTASection.tsx';

const HomePage = () => {
  return (
    <div>
      <HeroSection />
      <FeaturesSection />
      <PricingSection />
      <CTASection />
    </div>
  );
};

export default HomePage;