package com.changgou.filter;

import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关全局权限过滤器
 *
 * @author Alan
 * @version 1.0
 * @date 2019/11/23 14:26
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    //令牌的key
    private static final String AUTHORIZE_TOKEN = "Authorization";
    //默认跳到登录页面
    private static final String USER_LOGIN_URL="http://localhost:9001/oauth/login";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1、获取Request、Response对象-exchange.get...
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //2、获取请求的URI-request.getURI().getPath()
        String uri = request.getURI().getPath();
        //3、如果是登录请求-uri.startsWith，放行-chain.filter
        if (!URLFilter.hasAuthorize(uri)) {
            return chain.filter(exchange);
        } else {
            //4、如果是非登录请求
            //4.1 获取前端传入的令牌-从请求头中获取-request.getHeaders().getFirst
            String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
            if (StringUtils.isEmpty(token)) {
                //4.2 如果头信息中没有，从请求参数中获取-request.getQueryParams().getFirst
                token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
            }
            if (StringUtils.isEmpty(token)) {
                //4.3 如果请求参数中没有，从cookie中获取-request.getCookies()-取值前先判断不为空-getFirst
                HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
                if (cookie != null) {
                    token = cookie.getValue();
                }
            }
            if (StringUtils.isEmpty(token)) {
                //4.4 如果以上方式都取不到令牌-返回405错误-response.setStatusCode(405)-return response.setComplete
                //response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
                response.setStatusCode(HttpStatus.SEE_OTHER);
                //拼接url地址
                String url=USER_LOGIN_URL+"?FROM="+request.getURI();
                response.getHeaders().set("Location",url);//将请求地址带回
                return response.setComplete();
            } else {
                try {
                    // 4.5 如果获取到了令牌，解析令牌-JwtUtil.parseJWT，放行-chain.filter(exchange)
                    //Claims claims = JwtUtil.parseJWT(token);
                    //把令牌放在头文件中返回
                   // request.mutate().header(AUTHORIZE_TOKEN, claims.toString());
                    request.mutate().header(AUTHORIZE_TOKEN, "bearer " + token);
                } catch (Exception e) {
                    e.printStackTrace();
                    //无效的认证
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
