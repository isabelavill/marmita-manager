import { Routes, Route, Navigate } from "react-router-dom";
import { Login } from "./pages/Login";
import { Registrar } from "./pages/Registrar";
import { Clientes } from "./pages/Clientes";
import { ProtectedRoute } from "./components/ProtectedRoute";

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/registrar" element={<Registrar />} />
      <Route
        path="/clientes"
        element={
          <ProtectedRoute>
            <Clientes />
          </ProtectedRoute>
        }
      />
      <Route path="/" element={<Navigate to="/clientes" replace />} />
    </Routes>
  );
}

export default App;
