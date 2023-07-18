package com.lixiuxaing.seckill.controller;

import com.lixiuxaing.seckill.common.result.Result;
import com.lixiuxaing.seckill.entity.service.ISeckillService;
import com.lixiuxaing.seckill.exception.RrException;
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
        int killNum = 10;
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
}