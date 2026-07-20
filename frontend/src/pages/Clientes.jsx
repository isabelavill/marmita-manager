import { useState, useEffect } from "react";
import { clientesService } from "../services/clientesService";
import { useAuth } from "../context/useAuth";

const CLIENTE_VAZIO = { nome: "", email: "", telefone: "" };

export function Clientes() {
  const [clientes, setClientes] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const [form, setForm] = useState(CLIENTE_VAZIO);
  const [editandoId, setEditandoId] = useState(null);
  const { logout, usuario } = useAuth();

  async function carregarClientes() {
    try {
      const response = await clientesService.listar();
      setClientes(response.data);
    } catch (err) {
      if (err.response?.status === 401) return;
      setErro("Erro ao carregar clientes");
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect -- falso positivo conhecido:
    // carregarClientes é assíncrona, o setState só roda após o await (fetch de dados de API)
    carregarClientes();
  }, []);

  async function handleSubmit(e) {
    e.preventDefault();
    try {
      if (editandoId) {
        await clientesService.atualizar(editandoId, form);
      } else {
        await clientesService.criar(form);
      }
      setForm(CLIENTE_VAZIO);
      setEditandoId(null);
      carregarClientes();
    } catch (err) {
      setErro(err.response?.data?.mensagem || "Erro ao salvar cliente");
    }
  }

  function handleEditar(cliente) {
    setForm({
      nome: cliente.nome,
      email: cliente.email,
      telefone: cliente.telefone,
    });
    setEditandoId(cliente.id);
  }

  async function handleDeletar(id) {
    if (!confirm("Confirma exclusão desse cliente?")) return;
    try {
      await clientesService.deletar(id);
      carregarClientes();
    } catch (err) {
      if (err.response?.status === 409) {
        setErro(
          "Não é possível excluir: esse cliente possui pedidos vinculados",
        );
      } else {
        setErro("Erro ao excluir cliente");
      }
    }
  }

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

      <form
        onSubmit={handleSubmit}
        style={{ display: "flex", gap: 8, margin: "20px 0" }}
      >
        <input
          placeholder="Nome"
          value={form.nome}
          onChange={(e) => setForm({ ...form, nome: e.target.value })}
          required
        />
        <input
          placeholder="Email"
          type="email"
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
          required
        />
        <input
          placeholder="Telefone"
          value={form.telefone}
          onChange={(e) => setForm({ ...form, telefone: e.target.value })}
          required
        />
        <button type="submit">{editandoId ? "Salvar" : "Adicionar"}</button>
        {editandoId && (
          <button
            type="button"
            onClick={() => {
              setForm(CLIENTE_VAZIO);
              setEditandoId(null);
            }}
          >
            Cancelar
          </button>
        )}
      </form>

      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr style={{ borderBottom: "2px solid #ccc" }}>
            <th style={{ textAlign: "left", padding: 8 }}>Nome</th>
            <th style={{ textAlign: "left", padding: 8 }}>Email</th>
            <th style={{ textAlign: "left", padding: 8 }}>Telefone</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {clientes.map((cliente) => (
            <tr key={cliente.id} style={{ borderBottom: "1px solid #eee" }}>
              <td style={{ padding: 8 }}>{cliente.nome}</td>
              <td style={{ padding: 8 }}>{cliente.email}</td>
              <td style={{ padding: 8 }}>{cliente.telefone}</td>
              <td style={{ padding: 8 }}>
                <button onClick={() => handleEditar(cliente)}>Editar</button>{" "}
                <button onClick={() => handleDeletar(cliente.id)}>
                  Excluir
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
