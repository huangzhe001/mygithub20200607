package com.changgou.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/12/1 19:41
 */
@Component//秒杀支付消息监听器
public class SeckillOrderPayMessageListener {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SeckillOrderService seckillOrderService;



    /**
     * 支付中心的监听器
     *
     * @param json
     */
    @RabbitListener(queues = "${mq.pay.queue.seckillorder}")
    public void payListener(String json) {
        //将数据转换成map
        Map<String, String> map = JSON.parseObject(json, Map.class);
        String return_code = map.get("return_code");
        //如果调用成功
        if (return_code.equalsIgnoreCase("success")) {
            //获取业务结果
            String result_code = map.get("result_code");
            //获取订单号
            String out_trade_no = map.get("out_trade_no");
            //获取交易流水号
            String transaction_id = map.get("transaction_id");
            //获取附加参数
            String attach = map.get("attach");
            //转换成map
            Map<String,String> attachMap = JSON.parseObject(attach, Map.class);
            String username = attachMap.get("username");
            //支付成功，修改订单状态
            if (result_code.equalsIgnoreCase("success")) {

               seckillOrderService.updateStatus(out_trade_no, transaction_id,username);
            } else {
                //支付失败，删除订单
               seckillOrderService.closeOrder(username);
            }
        }
    }
}
