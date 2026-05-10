import { useNavigate } from "react-router-dom";

export default function Documents() {
    const navigate = useNavigate();
    return (
      <>
        <div>documents</div>
        <button onClick={() => navigate('/identityVerify')}>Siguiente</button>
      </>
    )
  }