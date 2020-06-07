package com.changgou.oauth.interceptor;

import com.changgou.oauth.util.JwtToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Feign调用拦截器-会在调用所有Feign前执行apply方法的逻辑
 *
 * @author Alan
 * @version 1.0
 * @date 2019/11/25 21:08
 */
@Configuration
public class FeignOauth2RequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
     //创建管理员令牌
        String adminToken ="bearer "+ JwtToken.adminJwt();
        //将管理员令牌放入头文件中
        requestTemplate.header("Authorization",adminToken);

        //使用RequestContextHolder 工具获取了request相关变量
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes!=null){
            //取出request
            HttpServletRequest request = requestAttributes.getRequest();
            //获取所有头文件的key
            Enumeration<String> headerNames = request.getHeaderNames();
            while(headerNames.hasMoreElements()){
                //头文件的key
                String name = headerNames.nextElement();
                //获取头文件的value
                String values = request.getHeader(name);
                //将令牌数据添加到头文件中
                requestTemplate.header(name,values);
            }
        }
    }
}
