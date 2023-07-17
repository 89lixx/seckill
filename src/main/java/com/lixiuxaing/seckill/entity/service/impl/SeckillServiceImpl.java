package com.lixiuxaing.seckill.entity.service.impl;

import com.lixiuxaing.seckill.common.result.Result;
import com.lixiuxaing.seckill.entity.Seckill;
import com.lixiuxaing.seckill.entity.service.ISeckillService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: lixiuxiang3
 * @Date: 2023/7/17 17:55
 * @Version: 1.0
 */
@Service("seckillService")
public class SeckillServiceImpl implements ISeckillService {

//    private DynamicQuery

    @Override
    public List<Seckill> getSeckillList() {
        return null;
    }

    @Override
    public Seckill getById(long seckillId) {
        return null;
    }

    @Override
    public Long getSeckillCount(long seckillId) {
        return null;
    }

    @Override
    public void deleteSeckill(long seckillId) {

    }

    @Override
    public Result startSeckill(long seckillId, long userId) {
        return null;
    }
}
