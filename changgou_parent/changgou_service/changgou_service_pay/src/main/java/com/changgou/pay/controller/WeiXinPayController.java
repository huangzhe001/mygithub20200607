package com.changgou.pay.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeiXinPayServcie;
import com.github.wxpay.sdk.WXPayUtil;
import entity.Result;
import entity.StatusCode;
import entity.TokenDecode;
import org.apache.commons.io.IOUtils;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/28 16:35
 */
@RestController
@RequestMapping("/weixin/pay")
public class  WeiXinPayController {
    @Autowired
    private WeiXinPayServcie weiXinPayServcie;


    /**
     * 创建二维码
     *
     * @param paramMap
     * @return
     */
    @RequestMapping("/create/native")
    public Result createNative(@RequestParam Map<String, String> paramMap) {
       // String username = TokenDecode.getUserInfo().get("username");
        //便于测试先将username写死
        String username="zhangsan";
        paramMap.put("username",username);
        Map resultMap = weiXinPayServcie.createNative(paramMap);
        return new Result<>(true, StatusCode.OK, "生成二维码成功", resultMap);
    }

    /**
     * 查询支付状态
     *
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/status/query")
    public Result<Map> queryPayStatus(String out_trade_no) {
        Map map = weiXinPayServcie.queryPayStatus(out_trade_no);
        return new Result<>(true, StatusCode.OK, "查询支付状态成功", map);
    }

    @Value("${mq.pay.exchange.order}")
    private String orderExchange;
    @Value("${mq.pay.queue.order}")
    private String orderQueue;
    @Value("${mq.pay.routing.key}")
    private String orderRouting;
    @Value(("${mq.pay.routing.seckillkey}"))
    private String seckillRouting;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 读取支付回调数据
     *
     * @return
     */
    @RequestMapping("/notify/url")
    public String notifyUrl(HttpServletRequest request) {
        try {
            //读取支付回调数据
            ServletInputStream inputStream = request.getInputStream();
            //使用apache IOUtils把输入转换成字符
            String xmlResult = IOUtils.toString(inputStream, "UTF-8");
            //使用WXPayUtil将xml结果转换成map结构
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);

            //解析附加参数，获取交换机和路由key
            Map<String, String> attachMap = JSON.parseObject(resultMap.get("attach"), Map.class);
            String exchange = attachMap.get("exchange");
            String routingKey = attachMap.get("routingKey");
            System.out.println("微信回调参数为：" + resultMap);
            //发送mq消息
            rabbitTemplate.convertAndSend(exchange, routingKey, JSON.toJSONString(resultMap));
            //包装相应的数据
            Map<String, String> map = new HashMap<>();
            map.put("return_code", "SUCCESS");
            map.put("return_msg", "OK");
            //将map转换成xml格式的字符串返回
            return WXPayUtil.mapToXml(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //手动添加队列
    @RequestMapping("test/queue")
    public String testQueue() {
        //发送MQ消息
        //rabbitTemplate.convertAndSend(orderExchange, orderRouting, "{'return_code':'fail'}");
        rabbitTemplate.convertAndSend(orderExchange, seckillRouting, "{'return_code':'fail'}");
        return "ok";
    }

    /**关闭支付
     * @param orderId
     * @return
     */
    @RequestMapping("/closePay/{orderId}")
    public Map<String, String> closePay(@PathVariable Long orderId){
        Map<String, String> closePayMap = weiXinPayServcie.closePay(orderId);
        return closePayMap;
    }
}
