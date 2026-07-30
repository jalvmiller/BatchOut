package br.com.joaomu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.joaomu.entity.*;

import java.util.Optional;

// Optional = objeto ou null,
// List, usado no QuestaoRepository = lista vazia ou com conteúdo
/*
    JpaRepository é
    uma interface mãe que engloba os métodos do CrudRepository
    e PagingAndSortingRepository.
    Métodos como .save(), .findById(), .findAll() e .delete()
    são gerados dinamicamente em tempo de execução pelo
    Spring Data JPA através de proxies do Hibernate
*/

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
}
