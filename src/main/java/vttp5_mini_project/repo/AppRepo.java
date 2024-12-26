package vttp5_mini_project.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AppRepo {

    @Autowired
    @Qualifier("redis-string")
    RedisTemplate<String, String> redisTemplate; 

    // check if username (key) is in "users" redis hash
    public Boolean checkUsernameExistsInRedis(String username) {

        if (redisTemplate.opsForHash().hasKey("users", username)) {
            return true;
        }

        else {
            return false; 
        }

    }

    // check if username + password is correct 
    // return data as String
    public String getUserDataRedis(String username) {

        return (String) redisTemplate.opsForHash().get("users", username);

    }

    // create user 
    // insert username + password into redis 
    public void saveUserToRedis(String username, String userData) {

        redisTemplate.opsForHash().put("users", username, userData);

    }
    
}
