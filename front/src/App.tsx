import { BrowserRouter, Route, Routes } from "react-router-dom";

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
        <Route path="/" element={<OnBoarding />} />
        <Route path="/personalData" element={<Wizard />} />
        <Route path="/documents" element={<Documents />} />
        <Route path="/identityVerify" element={<IdentityVerify />} />
        <Route path="/sign" element={<Sign />} />
        <Route path="/payment" element={<Payment />} />
        <Route path="/wizard" element={<WizardProgress />} />
      </Routes>
    </BrowserRouter>
  );
}
