package vttp5_mini_project.config;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import jakarta.annotation.PostConstruct;

@Configuration
public class AppConfig {

    // logger
    private final Logger logger = Logger.getLogger(AppConfig.class.getName());

    // get redis config from application.properties
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private Integer redisPort;

    @Value("${spring.data.redis.username}")
    private String redisUsername;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Value("${spring.data.redis.database}")
    private Integer redisDatabase;

    // check values from application.properties are configured
    @PostConstruct
    public void logRedisConfig() {
        System.out.println("Redis Config:");
        System.out.println("Host: " + redisHost);
        System.out.println("Port: " + redisPort);
        System.out.println("Username: " + redisUsername);
        System.out.println("Password: " + (redisPassword != null ? "******" : "null"));
        System.out.println("Database: " + redisDatabase);

    }

    // string, string RedisTemplate
    @Bean("redis-string")
    public RedisTemplate<String, String> createRedisTemplate() {

        // create a database configuration 
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);

        // sets the database --> select 0 
        config.setDatabase(redisDatabase);

        // set the username and password if they are set 
        if (!redisUsername.trim().equals("")) {
            logger.info("Setting Redis username and password");
            config.setUsername(redisUsername);
            config.setPassword(redisPassword);

        }

        // create a connection to the database 
        JedisClientConfiguration jedisClient = JedisClientConfiguration.builder().build(); 

        // create a factory to connect to Redis 
        JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
        jedisFac.afterPropertiesSet();

        // create the RedisTemplate 
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>(); 
        redisTemplate.setConnectionFactory(jedisFac);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;

    }
    
}
