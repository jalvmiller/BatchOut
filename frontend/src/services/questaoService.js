import api from "./api";

// Todas as chamadas HTTP
// Todas as chamadas HTTP     GET; POST; DELETE
// Todas as chamadas HTTP

export const questaoService = {
  salvar: async (questao) => {
    const response = await api.post("/questoes", questao);
    return response.data;
  },

  excluir: async (id) => {
    const response = await api.delete(`/questoes/${id}`);
    return response.data;
  },

  atualizar: async (id, questao) => {
    const response = await api.put(`/questoes/${id}`, questao);
    return response.data;
  },

  busca: async (termo) => {
    const response = await api.get("/questoes", {
      params: termo ? { busca: termo } : {}
    });
    return response.data;
  },

  // Busca condicionada pelo ID, usada pela Dashboard
  listarTodas: async (busca) => {
    // busca fica entre chaves {}, caso contrário 
    // (busca) não é reconhecido como objeto, e 
    // sim como uma String pura

    // Se a variável 'busca' existir, adiciona 
    // ela como parâmetro na requisição. Caso contrário, 
    // envia {} e liste todas as questões
    const response = await api.get("/questoes", {
      params: busca ? { busca } : {}
    });

    return response.data;
  },

  iaSugerir: async (prompt, rascunhoEnunciado) => {
    const response = await api.post("/questoes/ia-sugerir", { prompt, rascunhoEnunciado });
    return response.data;
  },

  iaCriarTotal: async (prompt) => {
    const response = await api.post("/questoes/ia-criar-total", { prompt });
    return response.data;
  },

  uploadImagem: async (file) => {
    const formData = new FormData();
    formData.append("file", file);
    const response = await api.post("/midia/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    });
    return response.data;
  },

  upvoteQuestao: async (id) => {
    const response = await api.post(`/questoes/${id}/upvote`);
    return response.data;
  },

  getMeusUpvotes: async () => {
    const response = await api.get("/questoes/meus-upvotes");
    return response.data;
  },

  upvoteResolucao: async (id) => {
    const response = await api.post(`/questoes/resolucoes/${id}/upvote`);
    return response.data;
  },

  getMeusUpvotesResolucoes: async () => {
    const response = await api.get("/questoes/resolucoes/meus-upvotes");
    return response.data;
  }
};