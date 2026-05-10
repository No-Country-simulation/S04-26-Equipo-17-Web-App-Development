import { useNavigate } from "react-router-dom";

export default function PersonalData() {
    const navigate = useNavigate();

    return (
      <>
        <div>personalData</div>
        <button onClick={() => navigate('/documents')}>Siguiente</button>
      </>
    )
  }
