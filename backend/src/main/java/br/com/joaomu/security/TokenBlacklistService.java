package br.com.joaomu.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

// Redis tem a vantagem de ser muito mais rápido
// que um banco de dados relacional, justamente por armazenar dados em memória RAM
// Sendo ideal para autenticação e autorização..
// o blacklist funciona como uma lista de bloqueio,
// nesse caso, atua somente quando o logout é feito e o token
// perde validade. É algo que inviabiliza o uso de tokens roubados.

// Gerencia o Redis, portanto, Service
@Service
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;

    public TokenBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Adicionar o token na blacklist com tempo de vida (TTL)
    // TTL = Time To Live
    public void blacklistToken(String token, long timeToLiveInMillis) {
        String key = "blacklist:" + token;
        redisTemplate.opsForValue().set(key, "true", Duration.ofMillis(timeToLiveInMillis));
    }

    // Verificar se o token está na blacklist, se estiver
    // foi revogado e não pode mais ser usado
    public boolean isBlacklisted(String token) {
        String key = "blacklist:" + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
