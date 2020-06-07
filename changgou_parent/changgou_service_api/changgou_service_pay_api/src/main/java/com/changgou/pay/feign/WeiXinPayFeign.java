package com.changgou.pay.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/12/2 20:41
 */
@FeignClient(name = "pay")//name属性代表服务的提供者名字
@RequestMapping("/weixin/pay")
public interface WeiXinPayFeign {
    /**关闭支付
     * @param orderId
     * @return
     */
    @RequestMapping("/closePay/{orderId}")
    public Map<String, String> closePay(@PathVariable("orderId") Long orderId);
}
