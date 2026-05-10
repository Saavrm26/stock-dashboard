import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { fetchCurrentUser, login as apiLogin } from '@/app/lib/stockDashboardApiClient';
import { User } from '@/model/generated/v1/user_dto'; // Import the User interface

interface AuthContextType {
  user: User | null;
  isLoading: boolean;
  login: () => Promise<void>;
  showLoginPopup: boolean;
  setShowLoginPopup: (show: boolean) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [showLoginPopup, setShowLoginPopup] = useState(false);

  useEffect(() => {
    const checkUser = async () => {
      try {
        const currentUser = await fetchCurrentUser();
        setUser(currentUser);
      } catch (error) {
        console.error("Failed to fetch current user:", error);
        setUser(null);
        setShowLoginPopup(true); 
      } finally {
        setIsLoading(false);
      }
    };
    checkUser();
  }, []);

  const login = async () => {
    await apiLogin();
  };

  return (
    <AuthContext.Provider value={{ user, isLoading, login, showLoginPopup, setShowLoginPopup }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
