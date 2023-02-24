package top.rectorlee.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.rectorlee.domain.User;
import top.rectorlee.mapper.UserMapper;
import top.rectorlee.service.UserService;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Lee
 * @description
 * @date 2023-02-22  14:12:29
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String REDIS_USER_KEY_PREFIX = "user_";

    /**
     * 布隆过滤器
     * 参数一：表示将要丢给布隆过滤器的值是什么类型,决定了底层使用什么hash算法
     * 参数二：表示布隆数组的长度
     * 参数三：表示误判率
     */
    private BloomFilter bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), 10000000, 0.0001);

    @PostConstruct
    public void initBloomFiler() {
        List<String> idList = userMapper.findAllIds();
        idList.stream().forEach(id -> bloomFilter.put(id));
    }

    @Override
    public String findUserById(String id) {
        // 判断布隆过滤器中是否存在要查询的id,如果没有,则数据库中一定不存在
        if (!bloomFilter.mightContain(id)) {
            return "null";
        }

        // 查询redis中是否存在
        String userString = stringRedisTemplate.opsForValue().get(REDIS_USER_KEY_PREFIX + id);

        // redis中不存在
        if (StringUtils.isEmpty(userString)) {
            // 锁住当前要查询的数据的id
            synchronized (id.intern()) {
                // 继续查询redis,防止第一个人进来查询数据库后将数据缓存入redis中后面的人继续查询数据库
                String userStr = stringRedisTemplate.opsForValue().get(REDIS_USER_KEY_PREFIX + id);

                // 再次确认redis中没有数据,此时查询数据库
                if (StringUtils.isEmpty(userStr)) {
                    User user = userMapper.findUserById(id);
                    if (ObjectUtil.isEmpty(user)) {
                        // 将数据库和redis中不存在的数据缓存进redis中,防止缓存穿透(设置较短的过期时间)
                        stringRedisTemplate.opsForValue().set(REDIS_USER_KEY_PREFIX + id, "null", RandomUtil.randomInt(2, 10), TimeUnit.MINUTES);
                    } else {
                        // 为不同的key设置不同的过期时间,防止缓存雪崩
                        stringRedisTemplate.opsForValue().set(REDIS_USER_KEY_PREFIX + id, JSONObject.toJSONString(user), RandomUtil.randomInt(30, 60), TimeUnit.MINUTES);

                        return JSONObject.toJSONString(user);
                    }

                    return "null";
                } else {
                    return userStr;
                }
            }
        } else {
            // redis中存在,则直接返回
            return userString;
        }
    }
}
