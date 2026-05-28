"use client";

import { useEffect } from 'react';
import { useAuth } from '@/app/lib/AuthContext';
import ProfileView from '@/app/components/ProfileView';

export default function MePage() {
  const { user, isLoading, showLoginPopup, setShowLoginPopup } = useAuth();

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

  return <ProfileView user={user} />;
}