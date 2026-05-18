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
}
