package com.changgou.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.feign.WeiXinPayFeign;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import com.changgou.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/12/1 19:41
 */
@Component//秒杀支付消息监听器
@RabbitListener(queues = "${mq.pay.queue.seckillordertimer}")//监听真正接收数据的队列（非死信队列）
public class SeckillOrderDelayListener {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SeckillOrderService seckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private WeiXinPayFeign weiXinPayFeign;

    /**超时未支付秒杀订单处理
     * 读取消息，判断redis中是否有订单信息，如果有则说明未支付，
     * 此时需要先调用微信接口关闭支付，然后再删除订单
     * @param message
     */
    @RabbitHandler//监听到期消息转发到的队列
    public void getMessage(@Payload String message){
        //读取消息
        SeckillStatus seckillStatus = JSON.parseObject(message, SeckillStatus.class);
        //获取redis中的订单信息
        String username = seckillStatus.getUsername();
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);
        if (seckillOrder != null) {
            //如果订单存在，说明超时没有支付
            System.out.println("准备回滚库存"+seckillStatus);
            //先关闭支付
            Map<String, String> closeMap = weiXinPayFeign.closePay(seckillStatus.getOrderId());
            if (closeMap!=null &&closeMap.get("return_code").equalsIgnoreCase("success")
                    &&closeMap.get("result_code").equalsIgnoreCase("success")){
                //关闭订单
                seckillOrderService.closeOrder(username);
            }
        }
    }
}
