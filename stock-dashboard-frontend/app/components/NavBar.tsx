"use client";

import Link from 'next/link';
import { useAuth } from '@/app/lib/AuthContext';
import { login } from '../lib/stockDashboardApiClient';

export function NavBar() {
  const { user, isLoading, setShowLoginPopup } = useAuth();
  const isLoggedIn = !!user;

  const handleUserIconClick = (e: React.MouseEvent) => {
    if (!user) {
      e.preventDefault();
      setShowLoginPopup(true);
    }
  };

  const handleSignIn = async (e: React.MouseEvent) => {
    e.preventDefault();
    try {
      await login();
    } catch (err: any) {
      console.error("Login failed:", err);
    }
  };

  return (
    <nav className="fixed top-0 left-0 w-full z-50 flex justify-between items-center px-4 md:px-16 h-20 bg-surface/80 backdrop-blur-md border-b border-outline-variant">
      <div className="flex items-center gap-6 md:gap-12">
        <Link href="/" className="text-xl md:text-2xl font-black tracking-tighter text-primary">
          STOCK DASHBOARD
        </Link>
        <div className="hidden md:flex items-center gap-8">
          <Link className="text-primary font-medium text-sm md:text-base" href="#">
            Home
          </Link>
          <Link className="text-on-surface-variant hover:text-primary transition-colors text-sm md:text-base" href="#features">
            Features
          </Link>
        </div>
      </div>
      <div className="flex items-center gap-4 md:gap-6">
        {!isLoading && (
          isLoggedIn ? (
            <div className="relative">
              <Link href="/me" onClick={handleUserIconClick}>
                <div className="text-on-surface-variant hover:text-primary transition-colors text-sm md:text-base font-medium cursor-pointer">
                  Profile
                </div>
              </Link>
            </div>
          ) : (
            <>
              <button
                onClick={handleSignIn}
                className="text-on-surface-variant hover:text-primary transition-colors text-sm md:text-base font-medium"
              >
                Sign In
              </button>
              <button
                onClick={handleSignIn}
                className="px-5 py-2 bg-primary text-on-primary font-semibold rounded-lg hover:bg-secondary-fixed transition-all text-sm md:text-base"
              >
                Get Started
              </button>
            </>
          )
        )}
      </div>
    </nav>
  );
}