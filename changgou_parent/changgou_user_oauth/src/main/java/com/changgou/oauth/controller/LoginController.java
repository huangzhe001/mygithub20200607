package com.changgou.oauth.controller;

import com.changgou.oauth.service.LoginService;
import com.changgou.oauth.util.AuthToken;
import com.changgou.oauth.util.CookieUtil;
import entity.Result;
import entity.StatusCode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;


/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/24 21:27
 */
@RestController
@RequestMapping(value = "my")
public class LoginController {
    //客户端ID
    @Value("${auth.clientId}")
    private String clientId;
    //客户端秘钥
    @Value("${auth.clientSecret}")
    private String clientSecret;
    //cookie存储的域名
    @Value("${auth.cookieDomain}")
    private String cookieDomain;
    //cookie的生命周期
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;
    @Autowired
    private LoginService loginService;

    @PostMapping("login")
    public Result login(String username, String password) {

            if (StringUtils.isEmpty(username)) {
                return new Result(false, StatusCode.LOGINERROR, "用户名不能为空");
            }
            if (StringUtils.isEmpty(password)) {
                return new Result(false, StatusCode.LOGINERROR, "密码不能为空");
            }
            //调用service得到令牌
            AuthToken authToken = loginService.login(username, password, clientId, clientSecret);
            //获取accessToken
            String accessToken = authToken.getAccessToken();
            //调用下面方法将accessToken放入cookie中
            saveCookie(accessToken);
            //登录成功，将令牌返回
            return new Result(true,StatusCode.OK,"登录成功",accessToken);
    }

    private void saveCookie(String token) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response,cookieDomain,"/","Authorization" ,token,cookieMaxAge,false);
    }
}
