package com.lixiuxaing.seckill.entity.service.impl;

import com.lixiuxaing.seckill.common.dynamicquery.DynamicQuery;
import com.lixiuxaing.seckill.common.result.Result;
import com.lixiuxaing.seckill.entity.Seckill;
import com.lixiuxaing.seckill.entity.SuccessKilled;
import com.lixiuxaing.seckill.entity.service.ISeckillService;
import com.lixiuxaing.seckill.enums.SeckillStatusEnum;
import com.lixiuxaing.seckill.repository.SeckillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

/**
 * @Author: lixiuxiang3
 * @Date: 2023/7/17 17:55
 * @Version: 1.0
 */
@Service("seckillService")
public class SeckillServiceImpl implements ISeckillService {

    @Resource
    private DynamicQuery dynamicQuery;

    @Resource
    private SeckillRepository seckillRepository;


    @Override
    public List<Seckill> getSeckillList() {
        return seckillRepository.findAll();

    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillRepository.findOne(seckillId);
    }

    @Override
    public Long getSeckillCount(long seckillId) {
        String nativeSql = "SELECT count(*) FROM success_killed WHERE seckill_id=?";
        Object object = dynamicQuery.nativeQueryObject(nativeSql, new Object[]{seckillId});
        return ((Number)object).longValue();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSeckill(long seckillId) {
        String nativeSql = "DELETE FROM  success_killed WHERE seckill_id=?";
        dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{seckillId});
        nativeSql = "UPDATE seckill SET number =100 WHERE seckill_id=?";
        dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{seckillId});
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result startSeckill(long seckillId, long userId) {
        //校验库存
        String nativeSql = "SELECT number FROM seckill WHERE seckill_id=?";
        Object object = dynamicQuery.nativeQueryObject(nativeSql, new Object[]{seckillId});
        Long number = ((Number)object).longValue();
        if (number > 0) {
            nativeSql = "UPDATE seckill  SET number=number-1 WHERE seckill_id=?";
            dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{seckillId});

            // 创建订单
            SuccessKilled killed = new SuccessKilled();
            killed.setUserId(userId);
            killed.setSeckillId(seckillId);
            killed.setCreateTime(new Timestamp(System.currentTimeMillis()));
            killed.setState((short) 0);
            dynamicQuery.save(killed);

            /**
             * 这里仅仅是分表而已，提供一种思路，供参考，测试的时候自行建表
             * 按照用户 ID 来做 hash分散订单数据。
             * 要扩容的时候，为了减少迁移的数据量，一般扩容是以倍数的形式增加。
             * 比如原来是8个库，扩容的时候，就要增加到16个库，再次扩容，就增加到32个库。
             * 这样迁移的数据量，就小很多了。
             * 这个问题不算很大问题，毕竟一次扩容，可以保证比较长的时间，而且使用倍数增加的方式，已经减少了数据迁移量。
             */
//            String table = "success_killed_"+userId%8;
//            nativeSql = "INSERT INTO "+table+" (seckill_id, user_id,state,create_time)VALUES(?,?,?,?)";
//            Object[] params = new Object[]{seckillId,userId,(short)0,new Timestamp(System.currentTimeMillis())};
//            dynamicQuery.nativeExecuteUpdate(nativeSql,params);
            //支付
            return Result.ok(SeckillStatusEnum.SUCCESS);
        } else {
            return Result.error(SeckillStatusEnum.END);
        }
    }
}
