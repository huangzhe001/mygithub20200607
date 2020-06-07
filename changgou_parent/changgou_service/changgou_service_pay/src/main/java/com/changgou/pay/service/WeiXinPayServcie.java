package com.changgou.pay.service;

import java.util.Map;

/**
 * 生成微信支付二维码接口
 *
 * @author Alan
 * @version 1.0
 * @date 2019/11/28 15:53
 */
public interface WeiXinPayServcie {

    /**生成微信支付二维码
     * @param paramMap
     *                {out_trade_no 订单号,
     *                 total_fee 金额(分),
     *                   exchange 交换机,
     *                 routingKey 路由Key
     *                 }
     * @return
     */
    public Map createNative(Map<String, String> paramMap);

    /**
     * 查询支付状态
     *
     * @param out_trade_no//商户订单号
     * @return
     */
    public Map queryPayStatus(String out_trade_no);

    /**关闭支付
     * @param orderId
     * @return
     */
    Map<String, String> closePay(Long orderId);

}
