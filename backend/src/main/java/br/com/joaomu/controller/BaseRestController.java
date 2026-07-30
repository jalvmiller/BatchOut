package br.com.joaomu.controller;

import br.com.joaomu.service.CrudService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class BaseRestController<T, ID> {

    protected final CrudService<T, ID> service;

    public BaseRestController(CrudService<T, ID> service) {
        this.service = service;
    }

    // Listar todas trabalha com a busca por termo opcional (?busca=...)
    @GetMapping
    public ResponseEntity<List<T>> listarTodas(@RequestParam(required = false) String busca) {
        if (busca != null && !busca.isBlank()) {
            return ResponseEntity.ok(service.buscarPorTermo(busca));
        }
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> buscarPorId(@PathVariable ID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<T> criar(@RequestBody T entity) {
        T salva = service.salvar(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable ID id, @RequestBody T entity) {
        try {
            T atualizada = service.atualizar(id, entity);
            return ResponseEntity.ok(atualizada);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable ID id) {
        try {
            service.remover(id);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
