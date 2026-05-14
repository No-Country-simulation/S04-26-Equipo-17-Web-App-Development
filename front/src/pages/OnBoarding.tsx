import { useNavigate } from "react-router-dom";

export default function OnBoarding() {
  const navigate = useNavigate();
  return (
    <>
      <div>onBoarding</div>
      <button onClick={() => navigate("/personalData")}>Empezar OnBoarding</button>
    </>
  );
}
