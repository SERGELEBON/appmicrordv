package ci.hardwork.chatai.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${app.cache.redis.enabled:false}")
    private boolean redisEnabled;

    /**
     * Configuration Redis pour production/staging
     */
    @Bean
    @ConditionalOnProperty(name = "app.cache.redis.enabled", havingValue = "true")
    @Primary
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        log.info("Configuration du cache distribué Redis sur {}:{}", redisHost, redisPort);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer(createJacksonRedisSerializer()));

        // Configuration spécifique par cache
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Cache pour les titres générés - 30 minutes
        cacheConfigurations.put("conversation-titles", config.entryTtl(Duration.ofMinutes(30)));
        
        // Cache pour la disponibilité des modèles - 5 minutes
        cacheConfigurations.put("model-availability", config.entryTtl(Duration.ofMinutes(5)));
        
        // Cache pour les tokens utilisateur - 1 heure
        cacheConfigurations.put("user-tokens", config.entryTtl(Duration.ofHours(1)));
        
        // Cache pour les sessions actives - 24 heures
        cacheConfigurations.put("active-sessions", config.entryTtl(Duration.ofHours(24)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    /**
     * RedisTemplate pour opérations manuelles
     */
    @Bean
    @ConditionalOnProperty(name = "app.cache.redis.enabled", havingValue = "true")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(createJacksonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(createJacksonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Fallback vers Caffeine pour développement/test
     */
    @Bean
    @ConditionalOnProperty(name = "app.cache.redis.enabled", havingValue = "false", matchIfMissing = true)
    public CacheManager caffeineCacheManager() {
        log.info("Configuration du cache local Caffeine (mode développement)");
        
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheSpecification("maximumSize=1000,expireAfterWrite=10m");
        
        return cacheManager;
    }

    /**
     * Sérialiseur JSON pour Redis
     */
    private Jackson2JsonRedisSerializer<Object> createJacksonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        
        serializer.setObjectMapper(objectMapper);
        return serializer;
    }
}