import { Navigate, Outlet } from "react-router-dom";

import { useApp } from "@/context/appContext";

export default function ProtectedRoute() {
  const { session } = useApp();

  if (!session) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}
