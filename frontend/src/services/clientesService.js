import api from "./api";

export const clientesService = {
  listar: () => api.get("/clientes"),
  criar: (dados) => api.post("/clientes", dados),
  atualizar: (id, dados) => api.put(`/clientes/${id}`, dados),
  deletar: (id) => api.delete(`/clientes/${id}`),
};
