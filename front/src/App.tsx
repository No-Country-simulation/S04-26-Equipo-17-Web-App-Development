import { BrowserRouter, Route, Routes } from "react-router-dom";

import ProtectedRoute from "./components/ProtectedRoute.tsx";
import { Wizard } from "./components/wizard/Wizard.tsx";
import { WizardProgress } from "./components/wizard/WizardProgress.tsx";
import Documents from "./pages/Documents.tsx";
import IdentityVerify from "./pages/IdentityVerify.tsx";
import OnBoarding from "./pages/OnBoarding.tsx";
import Payment from "./pages/Payment.tsx";
import Sign from "./pages/Sign.tsx";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Ruta pública: el link de invitación llega con el token en la URL */}
        <Route path="/invite/:token" element={<OnBoarding />} />

        {/* Rutas protegidas: requieren sesión activa (onboardingId + sessionToken) */}
        <Route element={<ProtectedRoute />}>
          <Route path="/personalData" element={<Wizard />} />
          <Route path="/documents" element={<Documents />} />
          <Route path="/sign" element={<Sign />} />
          <Route path="/payment" element={<Payment />} />
          <Route path="/identityVerify" element={<IdentityVerify />} />
          <Route path="/wizard" element={<WizardProgress />} />
        </Route>

        {/* Cualquier ruta desconocida */}
        <Route
          path="*"
          element={
            <main className="flex min-h-dvh items-center justify-center bg-background text-foreground">
              <p>Invitación no encontrada</p>
            </main>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}
