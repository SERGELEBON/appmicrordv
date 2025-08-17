package ci.hardwork.gatwayapi.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    public JwtAuthenticationFilter() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            
            log.debug("Validation JWT pour la requête: {} {}", request.getMethod(), request.getURI());
            
            // Extraire le token JWT
            String token = extractToken(request);
            
            if (!StringUtils.hasText(token)) {
                log.warn("Token JWT manquant pour: {}", request.getURI());
                return handleUnauthorized(response, "Token JWT manquant");
            }
            
            try {
                // Valider et parser le token
                Claims claims = validateToken(token);
                
                if (claims == null) {
                    log.warn("Token JWT invalide pour: {}", request.getURI());
                    return handleUnauthorized(response, "Token JWT invalide");
                }
                
                // Ajouter les informations utilisateur dans les headers pour les services backend
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id", claims.getSubject())
                        .header("X-User-Email", claims.get("email", String.class))
                        .header("X-User-Role", claims.get("role", String.class))
                        .build();
                
                log.debug("Token JWT valide pour l'utilisateur: {} ({})", 
                         claims.get("email"), claims.get("role"));
                
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
                
            } catch (Exception e) {
                log.error("Erreur lors de la validation du token JWT: {}", e.getMessage());
                return handleUnauthorized(response, "Token JWT invalide: " + e.getMessage());
            }
        };
    }
    
    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
    
    private Claims validateToken(String token) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);
            
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
                    
        } catch (Exception e) {
            log.debug("Erreur de validation JWT: {}", e.getMessage());
            return null;
        }
    }
    
    private Mono<Void> handleUnauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        
        String errorResponse = String.format(
            "{\"error\":\"Unauthorized\",\"message\":\"%s\",\"timestamp\":\"%s\"}", 
            message, 
            java.time.Instant.now()
        );
        
        DataBuffer buffer = response.bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
    
    public static class Config {
        // Configuration du filtre (peut être étendue si nécessaire)
    }
}