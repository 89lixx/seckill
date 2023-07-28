package com.lxx.seckill.service;

import com.lxx.seckill.common.result.Result;

public interface ISeckillDistributedService {

    /**
     * 秒杀一：redis分布式锁
     * @param seckillId:
     * @param userId:
     * @return Result
     */
    Result startSeckillRedisLock(long seckillId, long userId);

    /**
     * 秒杀一：ZK分布式锁
     * @param seckillId:
     * @param userId:
     * @return Result
     */
    Result startSeckillZKLock(long seckillId, long userId);
}
