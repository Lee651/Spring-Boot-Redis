package top.rectorlee.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.nio.charset.Charset;

/**
 * @author Lee
 * @description
 * @date 2023-02-21  22:21:02
 */
@Component
public class MyStringSerializer implements RedisSerializer<String> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Charset charset;

    public MyStringSerializer() {
        this(Charset.forName("UTF8"));
    }

    public MyStringSerializer(Charset charset) {
        Assert.notNull(charset, "Charset must not be null!");
        this.charset = charset;
    }

    @Override
    public String deserialize(byte[] bytes) {
        String keyPrefix = "";
        String saveKey = new String(bytes, charset);
        int indexOf = saveKey.indexOf(keyPrefix);
        if (indexOf > 0) {
            // logger.info("key缺少前缀");
        } else {
            saveKey = saveKey.substring(indexOf);
        }
        // logger.info ("saveKey:{}",saveKey);
        return (saveKey.getBytes () == null ? null : saveKey);
    }

    @Override
    public byte[] serialize(String string) {
        String keyPrefix = "";
        String key = keyPrefix + string;
        // logger.info ("key:{},getBytes:{}",key, key.getBytes(charset));
        return (key == null ? null : key.getBytes (charset));
    }
}
