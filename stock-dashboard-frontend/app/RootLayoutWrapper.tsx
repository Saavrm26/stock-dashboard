"use client";

import { AuthProvider } from "./lib/AuthContext";
import { NavBar } from "./components/NavBar";
import { LoginPopup } from "./components/LoginPopup";
import Footer from "./components/Footer";

export default function RootLayoutWrapper({ children }: { children: React.ReactNode }) {
  return (
    <AuthProvider>
      <NavBar />
      {children}
      <Footer />
      <LoginPopup />
    </AuthProvider>
  );
}
