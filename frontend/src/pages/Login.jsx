import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../context/useAuth";

export function Login() {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setErro("");
    setCarregando(true);

    try {
      await login(email, senha);
      navigate("/clientes");
    } catch (err) {
      if (err.response?.status === 401 || err.response?.status === 400) {
        setErro("Email ou senha inválidos");
      } else {
        setErro("Erro ao conectar com o servidor");
      }
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div style={{ maxWidth: 400, margin: "80px auto" }}>
      <h1>Marmita Manager</h1>
      <h2>Login</h2>

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: 12 }}>
          <label>Email</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            style={{ width: "100%", padding: 8 }}
          />
        </div>

        <div style={{ marginBottom: 12 }}>
          <label>Senha</label>
          <input
            type="password"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            required
            style={{ width: "100%", padding: 8 }}
          />
        </div>

        {erro && <p style={{ color: "red" }}>{erro}</p>}

        <button
          type="submit"
          disabled={carregando}
          style={{ width: "100%", padding: 10 }}
        >
          {carregando ? "Entrando..." : "Entrar"}
        </button>
      </form>

      <p style={{ marginTop: 16 }}>
        Não tem conta? <Link to="/registrar">Cadastre-se</Link>
      </p>
    </div>
  );
}
