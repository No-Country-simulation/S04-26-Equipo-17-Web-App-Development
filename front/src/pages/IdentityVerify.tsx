import { useNavigate } from 'react-router-dom';

export default function IdentityVerify() {
    const navigate = useNavigate();
    return (
      <>
        <div>IdentityVerify</div>
        <button onClick={() => navigate('/sign')}>Siguiente</button>
      </>
    )
  }
