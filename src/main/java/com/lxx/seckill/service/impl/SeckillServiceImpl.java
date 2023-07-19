package com.lxx.seckill.service.impl;

import com.lxx.seckill.aop.ServiceLock;
import com.lxx.seckill.common.dynamicquery.DynamicQuery;
import com.lxx.seckill.common.result.Result;
import com.lxx.seckill.entity.Seckill;
import com.lxx.seckill.entity.SuccessKilled;
import com.lxx.seckill.service.ISeckillService;
import com.lxx.seckill.enums.SeckillStatusEnum;
import com.lxx.seckill.repository.SeckillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    @Resource ISeckillService seckillService;

    private Lock lock = new ReentrantLock(true);


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

            //支付
            return Result.ok(SeckillStatusEnum.SUCCESS);
        } else {
            return Result.error(SeckillStatusEnum.END);
        }
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Result startSeckillLock(long seckillId, long userId) {
//        lock.lock();
//        //校验库存
//        String nativeSql = "SELECT number FROM Seckill WHERE seckill_id=?";
//        Object object = dynamicQuery.nativeQueryObject(nativeSql, new Object[]{seckillId});
//        Long number = ((Number)object).longValue();
//
//        if (number > 0) {
//            nativeSql = "UPDATE Seckill  SET number=number-1 WHERE seckill_id=?";
//            dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{seckillId});
//
//            // 创建订单
//            SuccessKilled killed = new SuccessKilled();
//            killed.setUserId(userId);
//            killed.setSeckillId(seckillId);
//            killed.setCreateTime(new Timestamp(System.currentTimeMillis()));
//            killed.setState((short) 0);
//            dynamicQuery.save(killed);
//            lock.unlock();
//            //支付
//            return Result.ok(SeckillStatusEnum.SUCCESS);
//        } else {
//            lock.unlock();
//            return Result.error(SeckillStatusEnum.END);
//        }
//    }

    @Override
    public Result startSeckillLock(long seckillId, long userId) {

        try {
            lock.lock();
            // 这里就是没问题的，如果将lock放在函数（事物）内
            // 事务未结束，但是lock已经释放了，当将事务交付给数据库这段时间是没有锁的
            // 因此会出现超卖的情况，但是只会超卖1个
            return seckillService.startSeckillLockTransactional(seckillId, userId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return Result.ok(SeckillStatusEnum.SUCCESS);
    }

    @Override
    @Transactional
    public Result startSeckillLockTransactional(long seckillId, long userId) {
        String nativeSql = "SELECT number FROM seckill WHERE seckill_id=?";
        Object object = dynamicQuery.nativeQueryObject(nativeSql, new Object[]{seckillId});
        Long number = ((Number)object).longValue();

        if (number > 0) {
            nativeSql = "UPDATE seckill SET number=number-1 WHERE seckill_id=?";
            dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{seckillId});

            // 创建订单
            SuccessKilled killed = new SuccessKilled();
            killed.setUserId(userId);
            killed.setSeckillId(seckillId);
            killed.setCreateTime(new Timestamp(System.currentTimeMillis()));
            killed.setState((short) 0);
            dynamicQuery.save(killed);

            //支付
            return Result.ok(SeckillStatusEnum.SUCCESS);
        } else {
            return Result.error(SeckillStatusEnum.END);
        }
    }

    @Override
    @ServiceLock
    @Transactional(rollbackFor = Exception.class)
    public Result startSeckillAopLock(long seckillId, long userId) {
        String nativeSql = "SELECT number FROM seckill WHERE seckill_id=?";
        Object object = dynamicQuery.nativeQueryObject(nativeSql, new Object[]{seckillId});
        Long number = ((Number)object).longValue();

        if (number > 0) {
            nativeSql = "UPDATE seckill SET number=number-1 WHERE seckill_id=?";
            dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{seckillId});

            // 创建订单
            SuccessKilled killed = new SuccessKilled();
            killed.setUserId(userId);
            killed.setSeckillId(seckillId);
            killed.setCreateTime(new Timestamp(System.currentTimeMillis()));
            killed.setState((short) 0);
            dynamicQuery.save(killed);

            //支付
            return Result.ok(SeckillStatusEnum.SUCCESS);
        } else {
            return Result.error(SeckillStatusEnum.END);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result startSeckillDataLock(long seckillId, long userId) {
        String nativeSql = "SELECT number FROM seckill WHERE seckill_id=? FOR UPDATE";
        Object object = dynamicQuery.nativeQueryObject(nativeSql, new Object[]{seckillId});
        Long number = ((Number)object).longValue();

        if (number > 0) {
            nativeSql = "UPDATE seckill SET number=number-1 WHERE seckill_id=?";
            dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{seckillId});

            // 创建订单
            SuccessKilled killed = new SuccessKilled();
            killed.setUserId(userId);
            killed.setSeckillId(seckillId);
            killed.setCreateTime(new Timestamp(System.currentTimeMillis()));
            killed.setState((short) 0);
            dynamicQuery.save(killed);

            //支付
            return Result.ok(SeckillStatusEnum.SUCCESS);
        } else {
            return Result.error(SeckillStatusEnum.END);
        }
    }
}
