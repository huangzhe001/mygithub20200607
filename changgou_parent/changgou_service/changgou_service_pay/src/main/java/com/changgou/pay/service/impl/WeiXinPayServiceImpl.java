package com.changgou.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeiXinPayServcie;
import com.github.wxpay.sdk.WXPayUtil;
import entity.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付实现
 *
 * @author Alan
 * @version 1.0
 * @date 2019/11/28 15:58
 */
@Service
public class WeiXinPayServiceImpl implements WeiXinPayServcie {
    @Value("${weixin.appid}")
    private String appid;
    @Value("${weixin.partner}")
    private String partner;
    @Value("${weixin.partnerkey}")
    private String partnerkey;
    @Value("${weixin.notifyurl}")
    private String notifyurl;

    @Override
    public Map createNative(Map<String, String> paramMap) {
        //此map用于封装微信接口返回的内容
        Map<String, String> map = new HashMap<>();
        try {
            //组装微信接口需要的参数（参考接口文档）
            Map<String, String> param = new HashMap<>();
            param.put("appid", appid);//公众账号ID
            param.put("mch_id", partner);//商户号
            param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            param.put("body", "畅购商城");//商品描述
            param.put("out_trade_no", paramMap.get("out_trade_no"));//商户订单号
            param.put("total_fee", paramMap.get("total_fee"));//总金额
            param.put("spbill_create_ip", "127.0.0.1");//终端IP(随意写)
            param.put("notify_url", notifyurl);//回调地址
            param.put("trade_type", "NATIVE ");//交易类型：二维码支付

            //附加参数
            Map<String,String>attachMap=new HashMap<>();
            attachMap.put("exchange",paramMap.get("exchange"));
            attachMap.put("routingKey",paramMap.get("routingKey"));
            attachMap.put("username",paramMap.get("username"));
            //将附加参数放入param
            param.put("attach", JSON.toJSONString(attachMap));
            //生成xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("调用微信统一下单接口，参数为：" + xmlParam);
            //调用微信统一下单接口
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";//统一下单url
            HttpClient client = new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            //获取结果并解析
            String xmlResult = client.getContent();
            System.out.println("调用微信统一下单接口成功，结果为：" + xmlResult);
            //将xml转换成map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);
            String code_url = resultMap.get("code_url");//获取二维码的链接地址
            //将二维码链接地址，商户订单号，以及总金额封装到map并返回
            map.put("code_url", code_url);
            map.put("out_trade_no", paramMap.get("out_trade_no"));//商户订单号
            map.put("total_fee", paramMap.get("total_fee"));//总金额
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 查询支付状态
     *
     * @param out_trade_no//商户订单号
     * @return
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {
        //封装查询所需要的参数（参考文档）
        Map<String, String> param = new HashMap();
        try {
            param.put("appid", appid);//公众号Id
            param.put("mch_id", partner);//商户号
            param.put("out_trade_no", out_trade_no);//订单id
            param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            //调用工具生成xml
            String paramXml = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("调用微信查询订单接口，参数为：" + paramXml);
            //使用HttpClient发送请求
            String url = "https://api.mch.weixin.qq.com/pay/orderquery";//查询订单地址（参考文档）
            HttpClient client = new HttpClient(url);
            client.setXmlParam(paramXml);
            client.setHttps(true);
            client.post();
            String resultXml = client.getContent();
            System.out.println("调用微信查询订单接口成功，结果为：" + resultXml);
            //转换成map返回
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**调用微信接口关闭支付
     * @param orderId
     * @return
     */
    @Override
    public Map<String, String> closePay(Long orderId) {
        try {
            //封装查询所需要的参数（参考文档）
            Map<String, String> param = new HashMap();
            param.put("appid", appid);//公众号Id
            param.put("mch_id", partner);//商户号
            param.put("out_trade_no", String.valueOf(orderId));//订单id
            param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            //将map数据转换成xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("调用微信删除订单接口，参数为：" + xmlParam);
            //使用HttpClient发送请求
            String url = "https://api.mch.weixin.qq.com/pay/closeorder";//关闭订单地址（参考文档）
            HttpClient client = new HttpClient(url);
            client.setXmlParam(xmlParam);
            client.setHttps(true);
            client.post();
            String resultXml = client.getContent();
            System.out.println("调用微信删除订单接口成功，结果为：" + resultXml);
            //转换成map返回
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
