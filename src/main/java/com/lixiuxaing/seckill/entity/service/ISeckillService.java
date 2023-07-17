package com.lixiuxaing.seckill.entity.service;

import com.lixiuxaing.seckill.common.result.Result;
import com.lixiuxaing.seckill.entity.Seckill;

import java.util.List;

public interface ISeckillService {
    /**
     * 查询全部秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     * @param seckillId:
     * @return Seckill
     */
    Seckill getById(long seckillId);

    /**
     * 查询秒杀售卖商品数量
     * @param seckillId:
     * @return Long
     */
    Long getSeckillCount(long seckillId);

    /**
     * 删除秒杀售卖商品记录
     * @param seckillId:
     * @return void
     */
    void deleteSeckill(long seckillId);

    Result startSeckill(long seckillId, long userId);
}
