import { useNavigate } from 'react-router-dom';

export default function Sign() {
    const navigate = useNavigate();
    return (
      <> 
        <div>sign</div>
        <button onClick={() => navigate('/identityVerify')}>Siguiente</button>
      </>
    )
  }
