import { Outlet } from "react-router-dom";

interface ProtectedRouteProps {
  nextpage?: string;
  requiredStep?: number;
}

export default function ProtectedRoute({
  nextpage,
  requiredStep,
}: ProtectedRouteProps) {
  // TODO: implementar lógica de protección de rutas
  void nextpage;
  void requiredStep;

  return <Outlet />;
}
