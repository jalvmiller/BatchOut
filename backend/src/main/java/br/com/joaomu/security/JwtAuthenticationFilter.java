package br.com.joaomu.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Vai interceptar todas as requisições que chegam na API. Herda de OncePerRequestFilter, ou seja,
// é executada por requisição HTTP
/*
    [requisição chega] --> A
    A --> B{possui header 'Authorization' com 'Bearer '?}
    B -- Não --> C [[[passa adiante filterChain.doFilter]]]
    B -- Sim --> D[extrai o token cortando os primeiros 7 caracteres]
    
    D --> E[extrai o username do Token via JwtUtil]
    E --> F{username não é nulo & usuário não está autenticado no contexto?}
    F -- Não --> C [[[passa adiante filterChain.doFilter]]]
    F -- Sim --> G[carrega o usuário do banco via UserDetailsService]
    
    G --> H{o token é válido?}
    H -- Não --> C [[[passa adiante filterChain.doFilter]]]
    H -- Sim --> I[cria UsernamePasswordAuthenticationToken]

    I --> J[guarda no SecurityContextHolder]
    J -->        C [[[passa adiante filterChain.doFilter]]]

    C --> K[Controller / Endpoint]
*/

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    // conexão do filtro ao validador de tokens e ao carregador de usuários, sem a
    // conexão.. o filtro não consegue extrair o username
    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            UserDetailsService userDetailsService,
            TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        // Pega o token enviado no cabeçalho (Authorization: Bearer <token>)

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
            // Se não tiver header de autorização ou não estiver no padrão Bearer <token>,
            // não autentica e segue o fluxo.
        }

        String token = authHeader.substring(7);
        // o header vem como "Authorization: Bearer <token>", temos a retirada do token
        // puro, o resto é removido

        // Checar se está na blacklist
        if (tokenBlacklistService.isBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token revogado");
            return;
        }

        String username = jwtUtil.extractUsername(token);
        // usa o jwtutil para extrair o username

        // Se ter username e ninguém ainda autenticado no contexto, prossegue com a
        // autenticação..
        // O SecurityContextHolder é onde o Spring Security guarda os detalhes de quem
        // está autenticado. Se o usuário já estiver logado, SecurityContextHolder
        // não vai estar nulo e o filtro vai simplesmente pular a autenticação e deixar
        // passar.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                // UsernamePasswordAuthenticationToken é a classe do Spring Security
                // que representa um usuário autenticado

                SecurityContextHolder.getContext().setAuthentication(authToken);
                // usuário colocado no contexto, logado para requisição
            }
        }

        filterChain.doFilter(request, response);
        // Esse é o método que passa para o próximo filtro.

        // O spring security trabalha com uma fila de filtros, cada filtro faz uma
        // tarefa específica,
        // que lida com tratamento de exceções como ExceptionTranslationFilter
        // Quando a requisição passa por esses filtros, o spring entrega ela para o
        // DispatcherServlet
        // que faz a análise da URL e encaminha pro Controller
    }
}