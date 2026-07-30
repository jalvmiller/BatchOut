package br.com.joaomu.service;

import java.util.List;

// CRUD genérico, T é o tipo da entidade e ID é o tipo do id

public interface CrudService<T, ID> {
    List<T> listarTodos();

    T buscarPorId(ID id);

    T salvar(T entity);

    T atualizar(ID id, T entity);

    void remover(ID id);

    List<T> buscarPorTermo(String busca);
}
