package br.com.joaomu.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

//import java.security.Key;
import javax.crypto.SecretKey;
import java.util.Date;

// JWT é basicamente: 
// Header (tipo de token + algoritmo de criptografia(HMAC-SHAA nesse caso))
// Payload ("Claims" que são informações: usuário, data de criação, expiração), 
// nesse caso, Claims do usuário: quem é subject(username) e quando expira
// Assinatura (vai garantir a integridade do token), é gerada usando o Header e o Payload,
// codificados em Base64 e aplicando uma SECRET_KEY.. se alguém altera o payload no cliente,
// a assinatura não bate mais, logo o token é inválido, já que o servidor vai validar com a 
// chave secreta.

@Component
public class JwtUtil {

    // minímo de 32 caracteres para equivaler a 256 bits
    @Value("${jwt.secret}") // Segurança para o spring, vai suar a chave secreta do application.properties
    private String SECRET_KEY;
    private final long EXPIRATION_TIME = 86400000; // 24 horas em ms

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes()); // CONVERSÃO HMAC-SHA, padrão do JWT.. geração de chave segura
                                                          // aqui a String vira uma chave segura de fato pronta pra ser
                                                          // usada na assinatura
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey()) // assinar o token com a chave
                .compact(); // token é gerado como string compacta, padrão da doc do JWT
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        // processo reverso da geração de token, recupera o usuário (subject)
    }

    public long getRemainingExpirationTime(String token) {
        try {
            Date expiration = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();

            long remaining = expiration.getTime() - System.currentTimeMillis();
            return Math.max(remaining, 0);
        } catch (Exception e) { // método para calcular o tempo restante do token
            return 0; // Se der erro, retorna 0, pois o token está inválido
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
