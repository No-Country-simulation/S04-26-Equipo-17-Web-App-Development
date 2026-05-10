import { BrowserRouter, Routes, Route } from 'react-router-dom';
import OnBoarding from './pages/OnBoarding.tsx';
import PersonalData from './pages/PersonalData.tsx';
import Documents from './pages/Documents.tsx';
import IdentityVerify from './pages/IdentityVerify.tsx';
import Sign from './pages/Sign.tsx';
import Payment from './pages/Payment.tsx';  

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<OnBoarding />} />
        <Route path="/personalData" element={<PersonalData />} />
        <Route path="/documents" element={<Documents />} />
        <Route path="/identityVerify" element={<IdentityVerify />} />
        <Route path="/sign" element={<Sign />} />
        <Route path="/payment" element={<Payment />} />
      </Routes>
    </BrowserRouter>
  );
}
