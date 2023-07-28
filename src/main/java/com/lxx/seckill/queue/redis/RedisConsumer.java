package com.lxx.seckill.queue.redis;

import com.lxx.seckill.common.redis.RedisUtil;
import com.lxx.seckill.common.result.Result;
import com.lxx.seckill.common.webSocket.WebSocketServer;
import com.lxx.seckill.enums.SeckillStatusEnum;
import com.lxx.seckill.service.ISeckillService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: lixiuxiang3
 * @Date: 2023/7/21 14:49
 * @Version: 1.0
 */
@Service
public class RedisConsumer {

    @Resource
    private ISeckillService seckillService;
    @Resource
    private RedisUtil redisUtil;

    public void receiveMessage(String message) {
        Thread thread = Thread.currentThread();
        System.out.println("Thread Nanem :"+thread.getName());
        String[] array = message.split(";");
        if (redisUtil.getValue(array[0]) == null) {
            Result result = seckillService.startSeckill(Long.parseLong(array[0]), Long.parseLong(array[1]));
            if(result.equals(Result.ok(SeckillStatusEnum.SUCCESS))){
                WebSocketServer.sendInfo("秒杀成功",array[0]);//推送给前台
            }else{
                WebSocketServer.sendInfo("秒杀失败",array[0]);//推送给前台
                redisUtil.cacheValue(array[0], "ok");//秒杀结束
            }
        }
    }
}
