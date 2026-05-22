import { createContext, useCallback, useContext, useState, type ReactNode } from "react";

const SESSION_KEY = "northpay_session";

interface AppSession {
  onboardingId: number;
  sessionToken: string;
  currentStep: number;
  fullName: string;
  email: string;
}

interface AppContextType {
  session: AppSession | null;
  setSession: (session: AppSession) => void;
  updateStep: (step: number) => void;
  updateFullName: (fullName: string) => void;
  clearSession: () => void;
}

const AppContext = createContext<AppContextType | null>(null);

function loadSession(): AppSession | null {
  try {
    const raw = sessionStorage.getItem(SESSION_KEY);
    return raw ? (JSON.parse(raw) as AppSession) : null;
  } catch {
    return null;
  }
}

function saveSession(session: AppSession) {
  sessionStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

export function AppProvider({ children }: { children: ReactNode }) {
  const [session, setSessionState] = useState<AppSession | null>(loadSession);

  const setSession = useCallback((next: AppSession) => {
    saveSession(next);
    setSessionState(next);
  }, []);

  const updateStep = useCallback((step: number) => {
    setSessionState((prev) => {
      if (!prev) return prev;
      const next = { ...prev, currentStep: step };
      saveSession(next);
      return next;
    });
  }, []);

  const updateFullName = useCallback((fullName: string) => {
    setSessionState((prev) => {
      if (!prev) return prev;
      const next = { ...prev, fullName };
      saveSession(next);
      return next;
    });
  }, []);

  const clearSession = useCallback(() => {
    sessionStorage.removeItem(SESSION_KEY);
    setSessionState(null);
  }, []);

  return (
    <AppContext.Provider value={{ session, setSession, updateStep, updateFullName, clearSession }}>
      {children}
    </AppContext.Provider>
  );
}

export function useApp() {
  const context = useContext(AppContext);
  if (!context) throw new Error("useApp must be used within AppProvider");
  return context;
}
