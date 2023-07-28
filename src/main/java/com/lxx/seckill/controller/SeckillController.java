package com.lxx.seckill.controller;

import com.lxx.seckill.common.result.Result;
import com.lxx.seckill.service.ISeckillService;
import com.lxx.seckill.exception.RrException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: lixiuxiang3
 * @Date: 2023/7/18 14:24
 * @Version: 1.0
 */
@Api(tags = "秒杀")
@RestController
@RequestMapping("/seckill")
public class SeckillController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeckillController.class);

    private static int corePoolSize = Runtime.getRuntime().availableProcessors();

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize,
            corePoolSize+1,
            10L, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(1000));

    @Resource
    private ISeckillService seckillService;

    @ApiOperation(value="秒杀一",nickname = "lxx")
    @PostMapping("/start")
    public Result start(long seckillId) {
        int killNum = 100;
        final CountDownLatch latch = new CountDownLatch(killNum);
        seckillService.deleteSeckill(seckillId);
        final long killId = seckillId;
        LOGGER.info("秒杀一开始（会出现超卖）");

        /**
         * 开启新线程之前，将RequestAttributes对象设置为子线程共享
         * 这里仅仅是为了测试，否则 IPUtils 中获取不到 request 对象
         * 用到限流注解的测试用例，都需要加一下两行代码
         */
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(sra, true);
        for (int i = 0; i < killNum; ++ i) {
            final long userId = i;
            Runnable task = () -> {
              try {
                  Result result = seckillService.startSeckill(killId, userId);
                  if (result != null) {
                      LOGGER.info("用户:{}{}",userId,result.get("msg"));
                  } else {
                      LOGGER.info("用户:{}{}",userId,"哎呦喂，人也太多了，请稍后！");
                  }
              } catch (RrException e) {
                  LOGGER.error("哎呀报错了{}",e.getMsg());
              }
              latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("一共秒杀出{}件商品",seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @ApiOperation(value="秒杀二",nickname = "lxx")
    @PostMapping("/startLock")
    public Result startLock(long seckillId) {
        int killNum = 1000;
        final CountDownLatch latch = new CountDownLatch(killNum);
        seckillService.deleteSeckill(seckillId);
        final long killId = seckillId;
        LOGGER.info("秒杀二开始（会出现超卖）");
        Long currentTime = System.currentTimeMillis();

        for (int i = 0; i < killNum; ++ i) {
            final long userId = i;
            Runnable task = () -> {
                try {
                    Result result = seckillService.startSeckillLock(killId, userId);
                    if (result != null) {
                        LOGGER.info("用户:{}{}",userId,result.get("msg"));
                    } else {
                        LOGGER.info("用户:{}{}",userId,"哎呦喂，人也太多了，请稍后！");
                    }
                } catch (RrException e) {
                    LOGGER.error("哎呀报错了{}",e.getMsg());
                }
                latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("一共秒杀出{}件商品",seckillCount);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("一共使用时间{}",endTime-currentTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @ApiOperation(value="秒杀二-- lock 切面实现",nickname = "lxx")
    @PostMapping("/startAopLock")
    public Result startAopLock(long seckillId) {
        int killNum = 1000;
        final CountDownLatch latch = new CountDownLatch(killNum);
        seckillService.deleteSeckill(seckillId);
        final long killId = seckillId;
        LOGGER.info("秒杀二开始（会出现超卖）");
        Long currentTime = System.currentTimeMillis();

        for (int i = 0; i < killNum; ++ i) {
            final long userId = i;
            Runnable task = () -> {
                try {
                    Result result = seckillService.startSeckillAopLock(killId, userId);
                    if (result != null) {
                        LOGGER.info("用户:{}{}",userId,result.get("msg"));
                    } else {
                        LOGGER.info("用户:{}{}",userId,"哎呦喂，人也太多了，请稍后！");
                    }
                } catch (RrException e) {
                    LOGGER.error("哎呀报错了{}",e.getMsg());
                }
                latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("一共秒杀出{}件商品",seckillCount);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("一共使用时间{}",endTime-currentTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }


    @ApiOperation(value="秒杀四-- 数据库被关锁",nickname = "lxx")
    @PostMapping("/startDataLock")
    public Result startDataLock(long seckillId) {
        int killNum = 1000;
        final CountDownLatch latch = new CountDownLatch(killNum);
        seckillService.deleteSeckill(seckillId);
        final long killId = seckillId;
        LOGGER.info("秒杀四开始");
        Long currentTime = System.currentTimeMillis();

        for (int i = 0; i < killNum; ++ i) {
            final long userId = i;
            Runnable task = () -> {
                try {
                    Result result = seckillService.startSeckillDataLock(killId, userId);
                    if (result != null) {
                        LOGGER.info("用户:{}{}",userId,result.get("msg"));
                    } else {
                        LOGGER.info("用户:{}{}",userId,"哎呦喂，人也太多了，请稍后！");
                    }
                } catch (RrException e) {
                    LOGGER.error("哎呀报错了{}",e.getMsg());
                }
                latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("一共秒杀出{}件商品",seckillCount);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("一共使用时间{}",endTime-currentTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @ApiOperation(value="秒杀五-- 数据库悲观锁 先去判断数据是否改变",nickname = "lxx")
    @PostMapping("/startDataLock2")
    public Result startDataLock2(long seckillId) {
        int killNum = 1000;
        final CountDownLatch latch = new CountDownLatch(killNum);
        seckillService.deleteSeckill(seckillId);
        final long killId = seckillId;
        LOGGER.info("秒杀四开始");
        Long currentTime = System.currentTimeMillis();

        for (int i = 0; i < killNum; ++ i) {
            final long userId = i;
            Runnable task = () -> {
                try {
                    Result result = seckillService.startSeckillDataLock2(killId, userId);
                    if (result != null) {
                        LOGGER.info("用户:{}{}",userId,result.get("msg"));
                    } else {
                        LOGGER.info("用户:{}{}",userId,"哎呦喂，人也太多了，请稍后！");
                    }
                } catch (RrException e) {
                    LOGGER.error("哎呀报错了{}",e.getMsg());
                }
                latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("一共秒杀出{}件商品",seckillCount);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("一共使用时间{}",endTime-currentTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @ApiOperation(value="秒杀六-- 数据库乐观锁，控制版本实现",nickname = "lxx")
    @PostMapping("/startDataLock3")
    public Result startDataLock3(long seckillId) {
        int killNum = 1000;
        final CountDownLatch latch = new CountDownLatch(killNum);
        seckillService.deleteSeckill(seckillId);
        final long killId = seckillId;
        LOGGER.info("秒杀四开始");
        Long currentTime = System.currentTimeMillis();

        for (int i = 0; i < killNum; ++ i) {
            final long userId = i;
            Runnable task = () -> {
                try {
                    Result result = seckillService.startSeckillDataLock3(killId, userId,1);
                    if (result != null) {
                        LOGGER.info("用户:{}{}",userId,result.get("msg"));
                    } else {
                        LOGGER.info("用户:{}{}",userId,"哎呦喂，人也太多了，请稍后！");
                    }
                } catch (RrException e) {
                    LOGGER.error("哎呀报错了{}",e.getMsg());
                }
                latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("一共秒杀出{}件商品",seckillCount);
            Long endTime = System.currentTimeMillis();
            LOGGER.info("一共使用时间{}",endTime-currentTime);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }
}
