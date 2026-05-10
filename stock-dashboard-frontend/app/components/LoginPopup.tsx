"use client";

import { useAuth } from "@/app/lib/AuthContext";

export function LoginPopup() {
  const { showLoginPopup, setShowLoginPopup, login } = useAuth();

  if (!showLoginPopup) return null;

  const handleLoginClick = async () => {
    setShowLoginPopup(false);
    await login(); 
  };

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full flex justify-center items-center z-50">
      <div className="bg-white p-6 rounded-lg shadow-xl max-w-sm mx-auto">
        <h2 className="text-2xl font-bold mb-4">You need to log in!</h2>
        <p className="mb-6">Please log in to access this feature.</p>
        <div className="flex justify-end space-x-4">
          <button 
            onClick={() => setShowLoginPopup(false)}
            className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50"
          >
            Close
          </button>
          <button 
            onClick={handleLoginClick}
            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50"
          >
            Log In
          </button>
        </div>
      </div>
    </div>
  );
}
