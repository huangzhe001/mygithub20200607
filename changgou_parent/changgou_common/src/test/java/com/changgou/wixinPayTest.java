package com.changgou;

import com.github.wxpay.sdk.WXPayUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/28 15:28
 */
public class wixinPayTest {
    @Test
    public void Test() throws Exception {
        String str = WXPayUtil.generateNonceStr();
        System.out.println("随机字符串："+str);
        Map<String, String> map=new HashMap<>();
        map.put("id","123456");
        map.put("name","alan");
        map.put("address","广东深圳");
        String signature = WXPayUtil.generateSignature(map, "szitheima");
        System.out.println("=============================");
        System.out.println(signature);
        System.out.println("=============================");
        String szitheima = WXPayUtil.generateSignedXml(map, "szitheima");
        System.out.println(szitheima);
        System.out.println("=============================");
        String s = WXPayUtil.mapToXml(map);
        System.out.println(s);
        System.out.println("=============================");
        Map<String, String> stringStringMap = WXPayUtil.xmlToMap(szitheima);
        System.out.println(stringStringMap);
    }
}
