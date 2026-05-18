<<<<<<< HEAD
import { Button } from '@/components/ui/button';
import { useNavigate } from 'react-router-dom'

export default function OnBoarding() {
    const navigate = useNavigate();
    return (
        <>
            <div>onBoarding</div>
            <Button variant={"default"} size={"sm"} onClick={() => navigate('/personalData')}>Empezar OnBoarding</Button>
        </>
    )
=======
import { useNavigate } from "react-router-dom";

export default function OnBoarding() {
  const navigate = useNavigate();
  return (
    <>
      <div>onBoarding</div>
      <button onClick={() => navigate("/personalData")}>Empezar OnBoarding</button>
    </>
  );
>>>>>>> 96e839de68616d4b07640ef4c3f63904482dc518
}
