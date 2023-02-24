package top.rectorlee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lee
 * @description
 * @date 2023-02-21  22:07:21
 */
@RestController
@RequestMapping("/redis")
public class RedisController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 设置String类型的key value
     * @param name
     * @param value
     * @return
     */
    @RequestMapping("/setString/{name}/{value}")
    public String setString(@PathVariable("name") String name, @PathVariable("value") String value) {
        redisTemplate.opsForValue().set(name, value);

        return "success";
    }

    /**
     * 根据key获取value
     * @param name
     * @return
     */
    @RequestMapping("/getString/{name}")
    public String getString(@PathVariable("name") String name) {
        return redisTemplate.opsForValue().get(name);
    }

    /**
     * 设置Hash类型的数据
     * @return
     */
    @RequestMapping("/setHash")
    public String setHash() {
        String name = "fruit";
        Map<String, String> map = new HashMap<>();
        map.put("A", "apple");
        map.put("B", "banana");
        map.put("C", "cherry");
        redisTemplate.opsForHash().putAll(name, map);
        return "success";
    }

    @RequestMapping("/getHash/{key1}/{key2}")
    public String getHash(@PathVariable("key1") String key1, @PathVariable("key2") String key2) {
        return redisTemplate.opsForHash().get(key1, key2).toString();
    }
}
