"use client";

import Link from 'next/link';
import { useAuth } from '@/app/lib/AuthContext';
import { FaUserCircle } from 'react-icons/fa'
import { login } from '../lib/stockDashboardApiClient';

export function NavBar() {
  const { user, isLoading, setShowLoginPopup } = useAuth();
  const isLoggedIn = !!user;
  const handleUserIconClick = (e: React.MouseEvent) => {
    if (!user) {
      e.preventDefault(); // Prevent navigation if not logged in
      setShowLoginPopup(true); // Open login popup
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
    <nav className="bg-gray-800 p-4 text-white flex justify-between items-center">
      <Link href="/" className="text-xl font-bold">
        Stock Dashboard
      </Link>
      <div className="flex items-center space-x-4">
        {!isLoading && (
          isLoggedIn ? (
            <div className="relative">
              <Link href="/me" onClick={handleUserIconClick}>
                <FaUserCircle className="text-2xl cursor-pointer hover:text-gray-300" />
              </Link>
            </div>
          ) : (
            <button
              onClick={handleSignIn}
              className="inline-flex w-full items-center justify-center rounded-lg bg-zinc-900 px-4 py-3 text-sm font-medium text-zinc-50 shadow-sm transition-colors hover:bg-zinc-800 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-zinc-900 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-200 dark:focus-visible:outline-zinc-100"
            >
              Sign in
            </button>
          )
        )}
      </div>
    </nav>
  );
}
