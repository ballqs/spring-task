package org.sparta.springtask.common.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.sparta.springtask.common.constants.Const.REDIS_PREFIX;

@Slf4j
@Profile("!test")
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${redis.cluster.nodes}")
    private String redisClusterNodes;

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(host);
        redisConfiguration.setPort(port);
        return new LettuceConnectionFactory(redisConfiguration);
    }

//    @Bean
//    public RedissonClient redissonClient() {
//        // 클러스터 노드 주소 목록 설정
//        List<String> nodes = Arrays.stream(redisClusterNodes.trim().split(","))
//                .map(node -> REDIS_PREFIX + node)
//                .collect(Collectors.toList());
//
//        // Redisson 클러스터 설정
//        Config config = new Config();
//        config.setTransportMode(TransportMode.NIO) // 성능 최적화 설정
//                .useClusterServers()
//                .setSslEnableEndpointIdentification(true) // SSL 검증 활성화
//                .setScanInterval(2000)                   // 클러스터 노드 스캔 간격
//                .setConnectTimeout(10000)                // 연결 타임아웃 (밀리초)
//                .setRetryAttempts(3)                     // 연결 재시도 횟수
//                .setRetryInterval(1500)                  // 재시도 간격 (밀리초)
//                .addNodeAddress(nodes.toArray(new String[0])); // 클러스터 노드 주소 설정
//
//        return Redisson.create(config);
//    }
}
