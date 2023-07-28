package com.lxx.seckill.controller;

import com.lxx.seckill.common.redis.RedisUtil;
import com.lxx.seckill.common.result.Result;
import com.lxx.seckill.queue.kafka.KafkaConsumer;
import com.lxx.seckill.queue.kafka.KafkaSender;
import com.lxx.seckill.queue.redis.RedisSender;
import com.lxx.seckill.service.ISeckillDistributedService;
import com.lxx.seckill.service.ISeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Author: lixiuxiang3
 * @Date: 2023/7/20 11:23
 * @Version: 1.0
 */

@Api(tags = "分布式秒杀")
@RestController
@RequestMapping("/seckillDistributed")
public class SeckillDistributedController {
    private final static Logger LOGGER = LoggerFactory.getLogger(SeckillDistributedController.class);

    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    //调整队列数 拒绝服务
    private static ThreadPoolExecutor executor  = new ThreadPoolExecutor(corePoolSize, corePoolSize+1, 10l, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10000));

    @Resource
    private ISeckillService seckillService;

    @Resource
    private ISeckillDistributedService seckillDistributedService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RedisSender redisSender;

    @Resource
    private KafkaSender kafkaSender;

    @ApiOperation(value="秒杀一(Rediss分布式锁)",nickname="lxx")
    @PostMapping("/startRedisLock")
    public Result startRedisLock(long seckillId){
        seckillService.deleteSeckill(seckillId);
        final long killId =  seckillId;
        LOGGER.info("开始秒杀一");
        for(int i=0;i<10000;i++){
            final long userId = i;
            Runnable task = () -> {
                Result result = seckillDistributedService.startSeckillRedisLock(killId, userId);
                LOGGER.info("用户:{}{}",userId,result.get("msg"));
            };
            executor.execute(task);
        }
        try {
            Thread.sleep(15000);
            Long  seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("一共秒杀出{}件商品",seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @ApiOperation(value="秒杀二(zookeeper分布式锁)",nickname="科帮网")
    @PostMapping("/startZkLock")
    public Result startZkLock(long seckillId) throws ExecutionException, InterruptedException {
        seckillService.deleteSeckill(seckillId);
        final long killId =  seckillId;
        LOGGER.info("开始秒杀二");
        List<Future<Result>> futures = new ArrayList<>();
        for(int i=0;i<10000; i++){
            final long userId = i;
            Callable<Result> task = () -> {
                Result result = seckillDistributedService.startSeckillZKLock(killId, userId);
                LOGGER.info("用户:{}{}",userId,result.get("msg"));
                return result;
            };

            futures.add(executor.submit(task));
        }
        for (Future<Result> future : futures) {
            future.get();
        }

//            Thread.sleep(10000);
        Long  seckillCount = seckillService.getSeckillCount(seckillId);
        LOGGER.info("一共秒杀出{}件商品",seckillCount);

        return Result.ok();
    }

    @ApiOperation(value="秒杀三（redis分布式队列)")
    @PostMapping("/startRedisQueue")
    public Result startRedisQueue(long seckillId) {
        redisUtil.cacheValue(seckillId+"", null); //秒杀结束
        seckillService.deleteSeckill(seckillId);
        final long killId = seckillId;
        LOGGER.info("开始秒杀三");
        for (int i = 0; i < 1000; ++ i) {
            final long userId = i;
            Runnable task = () -> {
                if (redisUtil.getValue(killId+"") == null) {
                    redisSender.sendChannelMessage("seckill", killId+";"+userId);
                } else {
                    // 秒杀结束
                }
            };
            executor.execute(task);
        }
        try {
            Thread.sleep(10000);
            redisUtil.cacheValue(killId+"", null);
            Long  seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("一共秒杀出{}件商品",seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @ApiOperation(value="秒杀四(Kafka分布式队列)",nickname="科帮网")
    @PostMapping("/startKafkaQueue")
    public Result startKafkaQueue(long seckillId){
        seckillService.deleteSeckill(seckillId);
        final long killId =  seckillId;
        LOGGER.info("开始秒杀四");
        for(int i=0;i<1000;i++){
            final long userId = i;
            Runnable task = () -> {
                if(redisUtil.getValue(killId+"")==null){
                    //思考如何返回给用户信息ws
                    kafkaSender.sendChannelMessage("seckill",killId+";"+userId);
                }else{
                    //秒杀结束
                }
            };
            executor.execute(task);
        }
        try {
            Thread.sleep(30000);
            redisUtil.cacheValue(killId+"", null);
            Long  seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("一共秒杀出{}件商品",seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }
}
