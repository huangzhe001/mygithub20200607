package com.changgou;

import entity.HttpClient;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/28 15:39
 */
public class HttpClientTest {
    @Test
    public void test(){
        try {
            String url="https://www.baidu.com";
            //HttpClient client=new HttpClient(请求的url地址);
            HttpClient client=new HttpClient(url);
            //client.setHttps(true);//是否是https协议
            client.setHttps(true);
            //client.setXmlParam(xmlParam);//发送的xml数据
            //client.post();//执行post请求
            client.post();
            //String result = client.getContent(); //获取结果
            String content = client.getContent();
            System.out.println(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
