package com.lxx.seckill.queue.kafka;

import com.lxx.seckill.common.redis.RedisUtil;
import com.lxx.seckill.common.result.Result;
import com.lxx.seckill.common.webSocket.WebSocketServer;
import com.lxx.seckill.enums.SeckillStatusEnum;
import com.lxx.seckill.service.ISeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: lixiuxiang3
 * @Date: 2023/7/21 17:34
 * @Version: 1.0
 */
@Component
public class KafkaConsumer {
    @Autowired
    private ISeckillService seckillService;

    @Autowired
    private RedisUtil redisUtil;
    /**
     * 监听seckill主题,有消息就读取
     * @param message
     */
    @KafkaListener(topics = {"seckill"})
    public void receiveMessage(String message){
        /**
         * 收到通道的消息之后执行秒杀操作
         */
        String[] array = message.split(";");
        if(redisUtil.getValue(array[0])==null){
            Result result = seckillService.startSeckill(Long.parseLong(array[0]), Long.parseLong(array[1]));
            if(result.equals(Result.ok(SeckillStatusEnum.SUCCESS))){
                WebSocketServer.sendInfo("秒杀成功", array[0]);
            }else{
                WebSocketServer.sendInfo("秒杀失败", array[0]);
                redisUtil.cacheValue(array[0], "ok");
            }
        }else{
            WebSocketServer.sendInfo(array[0], "秒杀失败");
        }
    }
}
