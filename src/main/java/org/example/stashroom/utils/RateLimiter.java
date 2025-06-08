package org.example.stashroom.utils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Component
public class RateLimiter {

    private final RedisTemplate<String, String> redisTemplate;
    private static final int MAX_REQUESTS = 3;
    private static final Duration TIME_WINDOW = Duration.ofMinutes(1);

    private static final String LUA_SCRIPT =
            "local current = redis.call('get', KEYS[1])\n" +
                    "if current and tonumber(current) > tonumber(ARGV[1]) then\n" +
                    "   return 0\n" +
                    "end\n" +
                    "current = redis.call('incr', KEYS[1])\n" +
                    "if tonumber(current) == 1 then\n" +
                    "   redis.call('expire', KEYS[1], ARGV[2])\n" +
                    "end\n" +
                    "return 1";

    public RateLimiter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryAcquire(String key) {
        List<String> keys = Collections.singletonList("rate_limit:" + key);
        Long result = redisTemplate.execute(
                RedisScript.of(LUA_SCRIPT, Long.class),
                keys,
                String.valueOf(MAX_REQUESTS),
                String.valueOf(TIME_WINDOW.getSeconds())
        );

        return result != null && result == 1;
    }
}
