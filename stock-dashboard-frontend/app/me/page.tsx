"use client";

import { useEffect } from 'react';
import { useAuth } from '@/app/lib/AuthContext';
import { useRouter } from 'next/navigation';
import Footer from '@/app/components/Footer';

export default function MePage() {
  const { user, isLoading, showLoginPopup, setShowLoginPopup } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isLoading && !user && !showLoginPopup) {
      setShowLoginPopup(true);
    }
  }, [user, isLoading, showLoginPopup, setShowLoginPopup]);

  if (isLoading) {
    return <div className="flex justify-center items-center h-screen text-xl">Loading user details...</div>;
  }

  if (!user) {
    return null; // LoginPopup will handle the prompt
  }

  return (
    <div className="min-h-screen bg-background text-on-surface">
      <div className="container mx-auto p-4">
        <h1 className="text-3xl font-bold mb-6">My Profile</h1>
        <div className="bg-white shadow-md rounded-lg p-6">
          <p className="text-lg mb-2"><span className="font-semibold">ID:</span> {user.id}</p>
          <p className="text-lg mb-2"><span className="font-semibold">Full Name:</span> {user.fullName}</p>
          <p className="text-lg mb-2"><span className="font-semibold">Email:</span> {user.email}</p>
          {/* Add more user details as needed */}
        </div>
      </div>
      <Footer />
    </div>
  );
}
