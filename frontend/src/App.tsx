import React from 'react';
import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider, useAuth } from './context/AuthContext';
import { NotificationProvider } from './context/NotificationContext';
import { LanguageProvider } from './context/LanguageContext';

// Common Components
import { PrototypeBanner } from './components/common/PrototypeBanner';
import { Navbar } from './components/common/Navbar';
import { Footer } from './components/common/Footer';
import { Sidebar } from './components/common/Sidebar';
import { ProtectedRoute } from './components/common/ProtectedRoute';

// Public Pages
import { LandingPage } from './pages/LandingPage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { VerifyOtpPage } from './pages/VerifyOtpPage';
import { ForgotPasswordPage } from './pages/ForgotPasswordPage';
import { MarketplacePage } from './pages/MarketplacePage';
import { LotDetailsPage } from './pages/LotDetailsPage';
import { PriceBenchmarkPage } from './pages/PriceBenchmarkPage';
import { WarehousesPage } from './pages/WarehousesPage';

// Farmer Pages
import { FarmerDashboard } from './pages/farmer/FarmerDashboard';
import { CreateLotPage } from './pages/farmer/CreateLotPage';
import { FarmerLotsPage } from './pages/farmer/FarmerLotsPage';
import { FarmerContractsPage } from './pages/farmer/FarmerContractsPage';
import { FarmerBookingsPage } from './pages/farmer/FarmerBookingsPage';

// Buyer Pages
import { BuyerDashboard } from './pages/buyer/BuyerDashboard';
import { BuyerBidsPage } from './pages/buyer/BuyerBidsPage';
import { BuyerContractsPage } from './pages/buyer/BuyerContractsPage';

// Operator Pages
import { OperatorDashboard } from './pages/operator/OperatorDashboard';
import { RegisterWarehousePage } from './pages/operator/RegisterWarehousePage';
import { OperatorBookingsPage } from './pages/operator/OperatorBookingsPage';

// Admin Pages
import { AdminDashboard } from './pages/admin/AdminDashboard';
import { AdminUsersPage } from './pages/admin/AdminUsersPage';
import { AdminDisputesPage } from './pages/admin/AdminDisputesPage';
import { AdminAuditLogsPage } from './pages/admin/AdminAuditLogsPage';
import { AdminBenchmarksPage } from './pages/admin/AdminBenchmarksPage';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
});

const Layout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, user } = useAuth();
  const location = useLocation();

  const isDashboardRoute =
    location.pathname.startsWith('/farmer') ||
    location.pathname.startsWith('/buyer') ||
    location.pathname.startsWith('/operator') ||
    location.pathname.startsWith('/admin');

  return (
    <div className="min-h-screen flex flex-col bg-slate-50 text-slate-900 font-sans antialiased selection:bg-emerald-500 selection:text-white">
      <PrototypeBanner />
      <Navbar />

      {isAuthenticated && isDashboardRoute ? (
        <div className="flex-1 flex max-w-7xl w-full mx-auto">
          <Sidebar />
          <main className="flex-1 p-4 sm:p-6 lg:p-8 overflow-y-auto max-w-5xl">
            {children}
          </main>
        </div>
      ) : (
        <main className="flex-1">{children}</main>
      )}

      <Footer />
    </div>
  );
};

export const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <LanguageProvider>
          <NotificationProvider>
            <BrowserRouter>
              <Layout>
                <Routes>
                  {/* Public Routes */}
                  <Route path="/" element={<LandingPage />} />
                  <Route path="/marketplace" element={<MarketplacePage />} />
                  <Route path="/lots/:id" element={<LotDetailsPage />} />
                  <Route path="/benchmarks" element={<PriceBenchmarkPage />} />
                  <Route path="/warehouses" element={<WarehousesPage />} />
                  <Route path="/login" element={<LoginPage />} />
                  <Route path="/register" element={<RegisterPage />} />
                  <Route path="/verify-otp" element={<VerifyOtpPage />} />
                  <Route path="/forgot-password" element={<ForgotPasswordPage />} />

                  {/* Farmer Routes */}
                  <Route element={<ProtectedRoute allowedRoles={['FARMER']} />}>
                    <Route path="/farmer/dashboard" element={<FarmerDashboard />} />
                    <Route path="/farmer/create-lot" element={<CreateLotPage />} />
                    <Route path="/farmer/lots" element={<FarmerLotsPage />} />
                    <Route path="/farmer/contracts" element={<FarmerContractsPage />} />
                    <Route path="/farmer/bookings" element={<FarmerBookingsPage />} />
                  </Route>

                  {/* Buyer Routes */}
                  <Route element={<ProtectedRoute allowedRoles={['BUYER']} />}>
                    <Route path="/buyer/dashboard" element={<BuyerDashboard />} />
                    <Route path="/buyer/bids" element={<BuyerBidsPage />} />
                    <Route path="/buyer/contracts" element={<BuyerContractsPage />} />
                  </Route>

                  {/* Warehouse Operator Routes */}
                  <Route element={<ProtectedRoute allowedRoles={['WAREHOUSE_OPERATOR']} />}>
                    <Route path="/operator/dashboard" element={<OperatorDashboard />} />
                    <Route path="/operator/register-warehouse" element={<RegisterWarehousePage />} />
                    <Route path="/operator/bookings" element={<OperatorBookingsPage />} />
                  </Route>

                  {/* Admin Routes */}
                  <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
                    <Route path="/admin/dashboard" element={<AdminDashboard />} />
                    <Route path="/admin/users" element={<AdminUsersPage />} />
                    <Route path="/admin/disputes" element={<AdminDisputesPage />} />
                    <Route path="/admin/audit-logs" element={<AdminAuditLogsPage />} />
                    <Route path="/admin/benchmarks" element={<AdminBenchmarksPage />} />
                  </Route>

                  {/* Catch-all redirect */}
                  <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
              </Layout>
            </BrowserRouter>
          </NotificationProvider>
        </LanguageProvider>
      </AuthProvider>
    </QueryClientProvider>
  );
};
