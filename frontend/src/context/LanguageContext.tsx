import React, { createContext, useContext, useState } from 'react';

type Language = 'en' | 'hi';

interface LanguageContextType {
  language: Language;
  setLanguage: (lang: Language) => void;
  t: (key: string) => string;
}

const translations: Record<Language, Record<string, string>> = {
  en: {
    appTitle: 'Smart Agricultural Marketplace',
    appSubtitle: 'Direct, verified farmer-to-buyer platform with simulated escrow security',
    login: 'Log In',
    register: 'Register',
    logout: 'Logout',
    dashboard: 'Dashboard',
    marketplace: 'Marketplace',
    benchmarks: 'Mandi Prices',
    warehouses: 'Warehouses',
    createLot: 'Create Produce Lot',
    myLots: 'My Lots',
    myBids: 'My Bids',
    contracts: 'Sale Contracts',
    disputes: 'Disputes',
    auditLogs: 'Audit Logs',
    notifications: 'Notifications',
    wheat: 'Wheat',
    paddy: 'Paddy',
    mustard: 'Mustard',
    quintal: 'Quintal',
    karnal: 'Karnal',
    hisar: 'Hisar',
    ambala: 'Ambala',
    rohtak: 'Rohtak',
    sirsa: 'Sirsa',
    panipat: 'Panipat',
    prototypeNotice: 'Prototype Demonstration Platform — Benchmark prices and Escrow payments are simulated.',
    active: 'Active',
    pendingVerification: 'Pending Verification',
    suspended: 'Suspended',
    fundEscrow: 'Fund Escrow',
    releasePayment: 'Release Payment',
    dispatchProduce: 'Record Dispatch',
    confirmDelivery: 'Confirm Delivery',
    raiseDispute: 'Raise Dispute',
  },
  hi: {
    appTitle: 'स्मार्ट कृषि बाज़ार (SAM)',
    appSubtitle: 'सुरक्षित एस्क्रो भुगतान के साथ किसानों और खरीदारों का सीधा मंच',
    login: 'लॉग इन करें',
    register: 'पंजीकरण',
    logout: 'लॉग आउट',
    dashboard: 'डैशबोर्ड',
    marketplace: 'मंडी बाज़ार',
    benchmarks: 'मंडी भाव',
    warehouses: 'गोदाम / वेयरहाउस',
    createLot: 'फसल लॉट बनाएं',
    myLots: 'मेरे लॉट',
    myBids: 'मेरी बोलियां',
    contracts: 'बिक्री अनुबंध',
    disputes: 'विवाद समाधान',
    auditLogs: 'ऑडिट लॉग',
    notifications: 'सूचनाएं',
    wheat: 'गेहूं (Wheat)',
    paddy: 'धान / चावल (Paddy)',
    mustard: 'सरसों (Mustard)',
    quintal: 'क्विंटल',
    karnal: 'करनाल',
    hisar: 'हिसार',
    ambala: 'अंबाला',
    rohtak: 'रोहतक',
    sirsa: 'सिरसा',
    panipat: 'पानीपत',
    prototypeNotice: 'प्रोटोटाइप प्रदर्शन — मंडी मूल्य और एस्क्रो भुगतान अनुकरण (सिमुलेशन) मोड में हैं।',
    active: 'सक्रिय',
    pendingVerification: 'सत्यापन लंबित',
    suspended: 'निलंबित',
    fundEscrow: 'एस्क्रो में राशि जमा करें',
    releasePayment: 'भुगतान जारी करें',
    dispatchProduce: 'डिस्पैच दर्ज करें',
    confirmDelivery: 'डिलीवरी पुष्टि करें',
    raiseDispute: 'विवाद दर्ज करें',
  },
};

const LanguageContext = createContext<LanguageContextType | undefined>(undefined);

export const LanguageProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [language, setLanguageState] = useState<Language>(
    (localStorage.getItem('sam_language') as Language) || 'en'
  );

  const setLanguage = (lang: Language) => {
    localStorage.setItem('sam_language', lang);
    setLanguageState(lang);
  };

  const t = (key: string): string => {
    return translations[language][key] || translations.en[key] || key;
  };

  return (
    <LanguageContext.Provider value={{ language, setLanguage, t }}>
      {children}
    </LanguageContext.Provider>
  );
};

export const useLanguage = () => {
  const context = useContext(LanguageContext);
  if (!context) {
    throw new Error('useLanguage must be used within a LanguageProvider');
  }
  return context;
};
