package com.changgou.user.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.user.pojo.User;
import com.changgou.user.service.UserService;
import com.github.pagehelper.PageInfo;
import entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/****
 * @Author:shenkunlin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    /***
     * User分页条件搜索实现
     * @param user
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@RequestBody(required = false) User user, @PathVariable int page, @PathVariable int size) {
        //调用UserService实现分页条件查询User
        PageInfo<User> pageInfo = userService.findPage(user, page, size);
        return new Result(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * User分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@PathVariable int page, @PathVariable int size) {
        //调用UserService实现分页查询User
        PageInfo<User> pageInfo = userService.findPage(page, size);
        return new Result<PageInfo>(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param user
     * @return
     */
    @PostMapping(value = "/search")
    public Result<List<User>> findList(@RequestBody(required = false) User user) {
        //调用UserService实现条件查询User
        List<User> list = userService.findList(user);
        return new Result<List<User>>(true, StatusCode.OK, "查询成功", list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @PreAuthorize(value = "hasAnyAuthority('admin')")//拥有admin角色的用户才可以访问此方法
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        //调用UserService实现根据主键删除
        userService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /***
     * 修改User数据
     * @param user
     * @param id
     * @return
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody User user, @PathVariable String id) {
        //设置主键值
        user.setUsername(id);
        //调用UserService实现修改User
        userService.update(user);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /***
     * 新增User数据
     * @param user
     * @return
     */
    @PostMapping
    public Result add(@RequestBody User user) {
        //调用UserService实现添加User
        userService.add(user);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    /***
     * 根据ID查询User数据
     * @param id
     * @return
     */
    @GetMapping({"/{id}", "/load/{id}"})
    public Result<User> findById(@PathVariable String id) {
        //调用UserService实现根据主键查询User
        User user = userService.findById(id);
        return new Result<User>(true, StatusCode.OK, "查询成功", user);
    }

    /***
     * 查询User全部数据
     * @return
     */
    @GetMapping
    public Result<List<User>> findAll() {
        //调用UserService实现查询所有User
        List<User> list = userService.findAll();
        return new Result<List<User>>(true, StatusCode.OK, "查询成功", list);
    }

    @GetMapping("login")
    public Result login(String username, String password, HttpServletResponse response) {
        //根据username查询数据库中的用户信息
        User user = userService.findById(username);
        //比较输入的密码和数据库中的密码是否匹配，如果相同就登录成功
        //使用工具类，判断用户输入的未加密密码，和数据库中的密文是否一样
        if (user == null) {
            return new Result(false, StatusCode.LOGINERROR, "用户名不存在！");
        }
        if (BCrypt.checkpw(password, user.getPassword())) {
            //生成令牌（添加载荷）
            Map<String, Object> tokenMap = new HashMap<>();
            tokenMap.put("role", "USER");
            tokenMap.put("flag", true);
            tokenMap.put("user", user);
            String token = JwtUtil.createJWT(UUID.randomUUID().toString(), JSON.toJSONString(tokenMap), null);
            //将令牌传递给客户端
            //1:放在请求头中
            response.addHeader("Authorization", token);
            //2:将令牌放在cookie中
            Cookie cookie = new Cookie("Authorization", token);
            //设置路径,防止cookie不能跨域共享
            cookie.setPath("/");
            response.addCookie(cookie);

            return new Result(true, StatusCode.OK, "登录成功", user);
        } else {
            //否则用户名或者密码匹配失败，登录失败
            return new Result(false, StatusCode.LOGINERROR, "用户名或密码错误！");
        }
    }

    @GetMapping("/points/add")
    public Result addPoints(@RequestParam("point") Integer point) {
        String username = TokenDecode.getUserInfo().get("username");
        userService.addPoints(point, username);
        return new Result(true,StatusCode.OK,"增加积分成功");
    }
}
