import { useState, useEffect } from "react";
import api from "../services/api";
import { useAuth } from "../context/useAuth";

export function Clientes() {
  const [clientes, setClientes] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const { logout, usuario } = useAuth();

  useEffect(() => {
    async function carregarClientes() {
      try {
        const response = await api.get("/clientes");
        setClientes(response.data);
      } catch (err) {
        if (err.response?.status === 401) {
          // Token expirado/inválido - o interceptor do api.js já vai redirecionar sozinho
          return;
        }
        setErro("Erro ao carregar clientes");
      } finally {
        setCarregando(false);
      }
    }

    carregarClientes();
  }, []);

  if (carregando) return <p>Carregando...</p>;

  return (
    <div style={{ maxWidth: 800, margin: "40px auto" }}>
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <h1>Clientes</h1>
        <div>
          <span style={{ marginRight: 12 }}>{usuario?.email}</span>
          <button onClick={logout}>Sair</button>
        </div>
      </div>

      {erro && <p style={{ color: "red" }}>{erro}</p>}

      <table
        style={{ width: "100%", borderCollapse: "collapse", marginTop: 20 }}
      >
        <thead>
          <tr style={{ borderBottom: "2px solid #ccc" }}>
            <th style={{ textAlign: "left", padding: 8 }}>Nome</th>
            <th style={{ textAlign: "left", padding: 8 }}>Email</th>
            <th style={{ textAlign: "left", padding: 8 }}>Telefone</th>
          </tr>
        </thead>
        <tbody>
          {clientes.map((cliente) => (
            <tr key={cliente.id} style={{ borderBottom: "1px solid #eee" }}>
              <td style={{ padding: 8 }}>{cliente.nome}</td>
              <td style={{ padding: 8 }}>{cliente.email}</td>
              <td style={{ padding: 8 }}>{cliente.telefone}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
