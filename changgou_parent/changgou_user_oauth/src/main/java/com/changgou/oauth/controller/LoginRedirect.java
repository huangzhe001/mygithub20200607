package com.changgou.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/26 20:40
 */
@Controller
@RequestMapping("oauth")
public class LoginRedirect {
    /**跳转到登录页面
     * @return
     */
    @GetMapping("login")
    public String login(@RequestParam(value = "FROM",required = false) String from, Model model){
        model.addAttribute("url",from);
        return "login";
    }
}
