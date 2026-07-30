package br.com.joaomu.service;

import br.com.joaomu.entity.Usuario;
import br.com.joaomu.repository.UsuarioRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements CrudService<Usuario, Long> {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + id));
    }

    @Override
    public Usuario salvar(Usuario entity) {
        return usuarioRepository.save(entity);
    }

    @Override
    public Usuario atualizar(Long id, Usuario entity) {
        Usuario existente = buscarPorId(id);
        if (entity.getNome() != null) {
            existente.setNome(entity.getNome());
        }
        if (entity.getEmail() != null) {
            existente.setEmail(entity.getEmail());
        }
        if (entity.getUsername() != null) {
            existente.setUsername(entity.getUsername());
        }
        return usuarioRepository.save(existente);
    }

    @Override
    public void remover(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado para remoção: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public List<Usuario> buscarPorTermo(String busca) {
        if (busca != null && !busca.isBlank()) {
            //
            return usuarioRepository.findByUsername(busca).map(List::of).orElseGet(List::of);
        }
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }
}
