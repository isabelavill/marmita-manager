import { useState } from "react";
import api from "../services/api";
import { AuthContext } from "./auth-context.js";

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(() => {
    const token = localStorage.getItem("token");
    const email = localStorage.getItem("email");
    return token && email ? { email } : null;
  });

  async function login(email, senha) {
    const response = await api.post("/auth/login", { email, senha });
    const { token } = response.data;

    localStorage.setItem("token", token);
    localStorage.setItem("email", email);
    setUsuario({ email });
  }

  async function registrar(email, senha) {
    const response = await api.post("/auth/registrar", { email, senha });
    const { token } = response.data;

    localStorage.setItem("token", token);
    localStorage.setItem("email", email);
    setUsuario({ email });
  }

  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("email");
    setUsuario(null);
  }

  return (
    <AuthContext.Provider value={{ usuario, login, registrar, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
