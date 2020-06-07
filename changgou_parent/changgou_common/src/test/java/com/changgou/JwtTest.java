package com.changgou;

import io.jsonwebtoken.*;
import org.junit.Test;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/23 13:26
 */
public class JwtTest {
    @Test
    public void testGreatJwt() {
        //1、创建Jwt构建器-jwtBuilder = Jwts.builder()
        JwtBuilder jwtBuilder = Jwts.builder();
        //2、设置唯一编号-setId
        jwtBuilder.setId("123456");
        //3、设置主题，可以是JSON数据-setSubject()
        jwtBuilder.setSubject("测试主题");
        //4、设置签发日期-setIssuedAt
        jwtBuilder.setIssuedAt(new Date());
        //5、设置签发人-setIssuer
        jwtBuilder.setIssuer("alan");
      /*  //设置过期时间为30秒
        Date exp = new Date(System.currentTimeMillis() + 30000);
        jwtBuilder.setExpiration(exp);*/
        //自定义claims
        Map<String, Object> user = new HashMap<>();
        user.put("name", "alan");
        user.put("age", "18");
        user.put("address", "广东深圳");
        jwtBuilder.addClaims(user);
        //6、设置签证
        jwtBuilder.signWith(SignatureAlgorithm.HS256, "itheima.alan");//第一个参数;签名算法，第二个参数：秘钥（盐）

        //7、生成令牌-compact()
        String token = jwtBuilder.compact();
        //8、输出结果
        System.out.println(token);
        //eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMjM0NTYiLCJzdWIiOiLmtYvor5XkuLvpopgiLCJpYXQiOjE1NzQ0ODcwNzgsImlzcyI6ImFsYW4ifQ.ICBEXXW4RXqWfKL8WFPy-Q4vC4_OonwD0TkecYissaQ
    }

    /**
     * 解析令牌
     */
    @Test
    public void testParseJwt() {
        //String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMjM0NTYiLCJzdWIiOiLmtYvor5XkuLvpopgiLCJpYXQiOjE1NzQ0ODcwNzgsImlzcyI6ImFsYW4ifQ.ICBEXXW4RXqWfKL8WFPy-Q4vC4_OonwD0TkecYissaQ";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMjM0NTYiLCJzdWIiOiLmtYvor5XkuLvpopgiLCJpYXQiOjE1NzQ0ODc4NDgsImlzcyI6ImFsYW4iLCJleHAiOjE1NzQ0ODc4NzgsImFkZHJlc3MiOiLlub_kuJzmt7HlnLMiLCJuYW1lIjoiYWxhbiIsImFnZSI6IjE4In0.yCpMBo-l7iK5NXeQ-5pbzVnCZAMK2F6dvQmmKSslWOY";

        //创建Jwt解析器
        JwtParser jwtParser = Jwts.parser();
        //设置签名
        jwtParser.setSigningKey("itheima.alan");
        //设置解析的密文，并读取结果
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        //输出结果
        System.out.println(claims);
    }
}
