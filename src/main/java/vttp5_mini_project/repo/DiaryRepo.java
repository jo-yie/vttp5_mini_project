package vttp5_mini_project.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DiaryRepo {

    @Autowired
    @Qualifier("redis-string")
    RedisTemplate<String, String> redisTemplate; 

    // save diary entry into redis in "<username>" hashmap 
    public void saveDiaryEntryToRedis(String username, String id, String diaryEntryData) {

        redisTemplate.opsForHash().put(username, id, diaryEntryData);

    }

    // get all diary entries from "<username>" hashmap 
    public List<Object> getAllDiaryEntries(String username) { 

        return redisTemplate.opsForHash().values(username);

    }

    
}
