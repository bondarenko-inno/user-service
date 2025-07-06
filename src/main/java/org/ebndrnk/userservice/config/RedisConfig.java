package org.ebndrnk.userservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuration class for Redis-related beans and serialization settings.
 */
@Configuration
public class RedisConfig {

    /**
     * Creates a {@link GenericJackson2JsonRedisSerializer} configured with support for Java 8 date/time types.
     *
     * @return a serializer that converts objects to JSON and back, handling Java time modules properly
     */
    @Bean
    public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    /**
     * Creates a RedisTemplate bean configured to use String keys and JSON-serialized values.
     *
     * @param connectionFactory the Redis connection factory to use
     * @return configured RedisTemplate with custom key and value serializers
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(genericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(genericJackson2JsonRedisSerializer());
        return template;
    }

}
