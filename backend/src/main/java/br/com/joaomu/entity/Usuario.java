package br.com.joaomu.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

// Para guardar permissões/role, a interface GrantedAuthority deve ser
// implementada, e a classe SimpleGrantedAuthority é uma implementação
// simples de GrantedAuthority que encapsula uma string como permissão.
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// ArrayList é usada para criar uma lista de permissões/authorities
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String nome;

    private String avatar;
    // foto de perfil do usuário (url)
    // usuarios/avatar/{id}.jpg

    @Column(unique = true)
    private String email;

    private Integer pontos = 0;

    private boolean especialista = false;

    private boolean administrador = false;

    // Construtor Padrão (NoArgsConstructor)
    public Usuario() {
    }

    // Construtor Completo (AllArgsConstructor)
    public Usuario(Long id, String username,
            String password,
            String nome,
            String email,
            String avatar,
            Integer pontos,
            boolean especialista,
            boolean administrador) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.email = email;
        this.avatar = avatar;
        this.pontos = pontos;
        this.especialista = especialista;
        this.administrador = administrador;
    }

    // Getters e Setters Manuais
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getPontos() {
        return pontos;
    }

    public void setPontos(Integer pontos) {
        this.pontos = pontos;
    }

    public boolean isEspecialista() {
        return especialista;
    }

    public void setEspecialista(boolean especialista) {
        this.especialista = especialista;
    }

    public boolean isAdministrador() {
        return administrador;
    }

    public void setAdministrador(boolean administrador) {
        this.administrador = administrador;
    }

    // Métodos exigidos pelo UserDetails
    // O getAuthorities retornava uma lista vazia antes das roles,
    // agora ele retorna uma lista de permissões baseada no tipo de usuário
    // Collection<? extends GrantedAuthority> significa que a coleção
    // retorna uma lista de objetos que implementam GrantedAuthority, ou seja,
    // qualquer objeto de uma classe que herde GrantedAuthority (conceito de
    // Wildcard) indicado pelo "?"
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Todo usuário autenticado tem a permissão padrão de usuário comum
        // As outras roles (especialista, administrador) são atribuídas condicionalmente
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (this.especialista) {
            authorities.add(new SimpleGrantedAuthority("ROLE_SPECIALIST"));
        }

        if (this.administrador) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return authorities;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
