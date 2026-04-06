package com.axiora.pec.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void shouldCreateObjectMapperWithJavaTimeSupport() throws Exception {
        AppConfig appConfig = new AppConfig();

        ObjectMapper objectMapper = appConfig.objectMapper();
        String json = objectMapper.writeValueAsString(
                java.time.Instant.parse("2026-04-06T12:00:00Z")
        );

        assertNotNull(objectMapper);
        assertTrue(json.contains("2026-04-06T12:00:00Z"));
    }

    @Test
    void shouldCreateTaskExecutor() {
        AppConfig appConfig = new AppConfig();

        ThreadPoolTaskExecutor executor =
                (ThreadPoolTaskExecutor) appConfig.taskExecutor();

        assertEquals(2, executor.getCorePoolSize());
        assertEquals(4, executor.getMaxPoolSize());
        assertEquals(100, executor.getQueueCapacity());
        assertTrue(executor.getThreadNamePrefix().startsWith("pec-async-"));
    }

    @Test
    void shouldCreateCacheManagerLoggerRunner() throws Exception {
        AppConfig appConfig = new AppConfig();
        CacheManager cacheManager = new ConcurrentMapCacheManager("rules");
        ApplicationArguments args = new DefaultApplicationArguments(new String[]{});

        assertDoesNotThrow(() ->
                appConfig.cacheManagerLogger(cacheManager).run(args));
    }

    @Test
    void shouldCreateRedisCacheManager() {
        RedisConfig redisConfig = new RedisConfig();
        LettuceConnectionFactory connectionFactory =
                new LettuceConnectionFactory("localhost", 6379);
        connectionFactory.afterPropertiesSet();

        try {
            CacheManager cacheManager = redisConfig.cacheManager(
                    connectionFactory,
                    new AppConfig().objectMapper()
            );

            assertInstanceOf(RedisCacheManager.class, cacheManager);
            assertTrue(cacheManager.getCacheNames().contains("rules"));
            assertTrue(cacheManager.getCacheNames().contains("auth-users"));
        } finally {
            connectionFactory.destroy();
        }
    }
}
