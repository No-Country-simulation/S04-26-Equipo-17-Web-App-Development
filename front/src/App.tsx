import { BrowserRouter, Routes, Route } from 'react-router-dom';
import OnBoarding from './pages/onBoarding.tsx';
import PersonalData from './pages/personalData.tsx';
import Documents from './pages/documents.tsx';
import IdentityVerify from './pages/identityVerify.tsx';
import Sign from './pages/sign.tsx';
import Payment from './pages/payment.tsx';  

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
