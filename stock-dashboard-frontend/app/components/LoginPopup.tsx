"use client";

import { useAuth } from '@/app/lib/AuthContext';

export function LoginPopup() {
  const { showLoginPopup, setShowLoginPopup, login } = useAuth();

  if (!showLoginPopup) return null;

  const handleLoginClick = async () => {
    setShowLoginPopup(false);
    await login();
  };

  return (
    <div className="fixed inset-0 bg-surface/80 backdrop-blur-sm overflow-y-auto h-full w-full flex justify-center items-center z-50">
      <div className="bg-surface-container p-6 md:p-8 rounded-2xl shadow-xl max-w-sm mx-auto border border-outline-variant">
        <h2 className="text-2xl font-bold mb-4 text-primary">You need to log in!</h2>
        <p className="mb-6 text-on-surface-variant">Please log in to access this feature.</p>
        <div className="flex justify-end space-x-4">
          <button
            onClick={() => setShowLoginPopup(false)}
            className="px-4 py-2 border border-outline-variant rounded-lg text-on-surface-variant hover:bg-surface-container-high focus:outline-none focus:ring-2 focus:ring-primary focus:ring-opacity-50 transition-colors"
          >
            Close
          </button>
          <button
            onClick={handleLoginClick}
            className="px-4 py-2 bg-primary text-on-primary rounded-lg hover:bg-secondary-fixed focus:outline-none focus:ring-2 focus:ring-primary focus:ring-opacity-50 transition-colors"
          >
            Log In
          </button>
        </div>
      </div>
    </div>
  );
}