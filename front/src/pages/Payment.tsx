import { useNavigate } from "react-router-dom";

export default function Payment() {
  const navigate = useNavigate();
  return (
    <>
      <div>payment</div>
      <button onClick={() => navigate("/")}>Siguiente</button>
    </>
  );
}
