package com.lxx.seckill.aop;

import com.lxx.seckill.distributedlock.redis.RedisLockUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * redis 分布式锁 AOp
 *
 * @Author: lixiuxiang3
 * @Date: 2023/7/19 15:40
 * @Version: 1.0
 */

@Component
@Scope
@Aspect
@Order(1)
public class RedisLockAspect {

    private static Lock lock = new ReentrantLock(true);

    // Service层切点
    @Pointcut("@annotation(com.lxx.seckill.aop.ServiceLock)")
    public void lockAspect() {

    }

    @Around("lockAspect()")
    public Object around(ProceedingJoinPoint joinPoint) {
//        boolean res = RedisLockUtil.tryLock()
        Object obj = null;
        try {
            obj = joinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            lock.unlock();
        }

        return obj;
    }
}
