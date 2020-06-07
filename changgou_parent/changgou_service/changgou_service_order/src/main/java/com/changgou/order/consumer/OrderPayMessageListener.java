package com.changgou.order.consumer;

import com.alibaba.fastjson.JSON;
import com.changgou.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/28 20:54
 */
@Component
public class OrderPayMessageListener {
    @Autowired
    private OrderService orderService;

    /**
     * 支付中心的监听器
     *
     * @param json
     */
    @RabbitListener(queues = "${mq.pay.queue.order}")
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
            //支付成功，修改订单状态
            if (result_code.equalsIgnoreCase("success")) {

                orderService.updateStatus(out_trade_no, transaction_id);
            } else {
                //支付失败，删除订单
                orderService.deleteOrder(out_trade_no);
            }
        }
    }
}
