package com.lixiuxaing.seckill.common.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.*;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @Author: lixiuxiang3
 * @Date: 2023/7/17 16:06
 * @Version: 1.0
 */
@Component
public class RedisUtil {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    @Resource
    private RedisTemplate<Serializable, Serializable> redisTemplate;

    /**
     * 前缀
     */
    public static final String KEY_PREFIX_VALUE = "itstyle:seckill:value:";

    /**
     * 缓存value操作
     *
     * @param k:
     * @param v:
     * @param time:
     * @return boolean
     */
    public boolean cacheValue(String k, Serializable v, long time) {
        String key = KEY_PREFIX_VALUE + k;
        try {
            ValueOperations<Serializable, Serializable> valueOps = redisTemplate.opsForValue();
            valueOps.set(key, v);
            if (time > 0) redisTemplate.expire(key, time, TimeUnit.SECONDS);
            return true;
        } catch (Throwable t) {
            logger.error("缓存[{}]失败, value[{}]", k, v, t);
        }
        return false;
    }

    /**
     * 缓存value操作
     *
     * @param k:
     * @param v:
     * @param time:
     * @param unit:
     * @return boolean
     */
    public boolean cacheValue(String k, Serializable v, long time, TimeUnit unit) {
        String key = KEY_PREFIX_VALUE + k;
        try {
            ValueOperations<Serializable, Serializable> valueOps = redisTemplate.opsForValue();
            valueOps.set(key, v);
            if (time > 0) redisTemplate.expire(key, time, unit);
            return true;
        } catch (Throwable t) {
            logger.error("缓存[{}]失败, value[{}]", k, v, t);
        }
        return false;
    }

    /**
     * 缓存value操作
     *
     * @param k:
     * @param v:
     * @return boolean
     */
    public boolean cacheValue(String k, Serializable v) {
        return cacheValue(k, v, -1);
    }

    /**
     * 判断缓存是否存在
     *
     * @param k:
     * @return boolean
     */
    public boolean containsValueKey(String k) {
        String key = KEY_PREFIX_VALUE + k;
        try {
            return redisTemplate.hasKey(key);
        } catch (Throwable t) {
            logger.error("判断缓存存在失败key[" + key + ", error[" + t + "]");
        }
        return false;
    }

    /**
     * 获取缓存
     *
     * @param k:
     * @return Serializable
     */
    public Serializable getValue(String k) {
        String key = KEY_PREFIX_VALUE + k;
        try {
            ValueOperations<Serializable, Serializable> valueOps = redisTemplate.opsForValue();
            return valueOps.get(key);
        } catch (Throwable t) {
            logger.error("获取缓存失败key[" + key + ", error[" + t + "]");
        }
        return null;
    }

    /**
     * 删除缓存
     *
     * @param k:
     * @return boolean
     */
    public boolean removeValue(String k) {
        String key = KEY_PREFIX_VALUE + k;
        try {
            redisTemplate.delete(key);
            return true;
        } catch (Throwable t) {
            logger.error("删除缓存失败key[" + key + ", error[" + t + "]");
        }
        return false;
    }

    /**
     * 递增
     *
     * @param k:
     * @param delta: 要增加几(大于0)
     * @return long
     */
    public long increase(String k, long delta) {
        String key = KEY_PREFIX_VALUE + k;
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(k, delta);
    }

    /**
     * 递减
     *
     * @param k:
     * @param delta: 要增加几(大于0)
     * @return long
     */
    public long decrease(String k, long delta) {
        String key = KEY_PREFIX_VALUE + k;
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(k, -delta);
    }
}
