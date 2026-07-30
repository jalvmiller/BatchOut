package br.com.joaomu.controller;

import br.com.joaomu.dto.AuthResponse;
import br.com.joaomu.dto.LoginRequest;
import br.com.joaomu.dto.RegisterRequest;
import br.com.joaomu.entity.Usuario;
import br.com.joaomu.repository.UsuarioRepository;
import br.com.joaomu.security.JwtUtil;
import br.com.joaomu.security.TokenBlacklistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService blacklistService;

    public AuthController(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder, JwtUtil jwtUtil, TokenBlacklistService blacklistService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.blacklistService = blacklistService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        Usuario user = new Usuario();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setNome(request.nome());
        user.setEmail(request.email());

        usuarioRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(new AuthResponse(token));
        // No cadastro, o usuário é criado e já recebe um token de autenticação
        // o token é devolvido pelo corpo da resposta AuthResponse
        // Retorna HTTP 201 Created caso o cadastro seja realizado normalmente
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        String token = jwtUtil.generateToken(request.username());

        return ResponseEntity.ok(new AuthResponse(token));
        // No login, o usuário é autenticado e recebe um token de autenticação
        // o token é devolvido pelo corpo da resposta AuthResponse
        // Retorna HTTP 200 OK caso o login seja realizado normalmente
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        // Retirar o token do header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Descobre quanto tempo ainda falta para o token expirar
            long remainingTime = jwtUtil.getRemainingExpirationTime(token);

            if (remainingTime > 0) {
                blacklistService.blacklistToken(token, remainingTime);
            }
        }

        return ResponseEntity.ok("Logout realizado com sucesso.");
    }

    // Retorna os dados do usuário logado
    @GetMapping("/me")
    public ResponseEntity<Usuario> me() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        return ResponseEntity.ok(user);
        // Retorna HTTP 200 OK caso o usuário seja encontrado
    }
}