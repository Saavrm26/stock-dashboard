"use client";

import { AuthProvider } from "./lib/AuthContext";
import { NavBar } from "./components/NavBar";
import { LoginPopup } from "./components/LoginPopup";

export default function RootLayoutWrapper({ children }: { children: React.ReactNode }) {
  return (
    <AuthProvider>
      <NavBar />
      {children}
      <LoginPopup />
    </AuthProvider>
  );
}
