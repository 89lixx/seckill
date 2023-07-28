package com.lxx.seckill.distributedlock.redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁工具类
 * @Author: lixiuxiang3
 * @Date: 2023/7/20 15:00
 * @Version: 1.0
 */
public class RedisLockUtil {
    private static RedissonClient redissonClient;

    public static void setRedissonClient(RedissonClient redissonClient) {
        RedisLockUtil.redissonClient = redissonClient;
    }


    /**
     * 加锁
     * @param lockKey:
     * @return RLock
     */
    public static RLock lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    /**
     * 释放锁
     * @param lockKey
     */
    public static void unlock(String lockKey) {
        redissonClient.getLock(lockKey).unlock();
    }

    public static void unlock(RLock lock) {
        lock.unlock();
    }

    /**
     * 设置过期时间
     * @param lockKey:
     * @param timeout:
     * @return RLock
     */
    public static RLock lock(String lockKey, int timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, TimeUnit.SECONDS);
        return lock;
    }

    /**
     * 尝试获取锁
     * @param lockKey:
     * @param waitTime: 最多等待时间
     * @param leaseTime: 上锁后自动释放锁时间
     * @return boolean
     */
    public static boolean tryLock(String lockKey, int waitTime, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * 尝试获取锁
     * @param lockKey
     * @param unit 时间单位
     * @param waitTime 最多等待时间
     * @param leaseTime 上锁后自动释放锁时间
     * @return
     */
    public static boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }


}
