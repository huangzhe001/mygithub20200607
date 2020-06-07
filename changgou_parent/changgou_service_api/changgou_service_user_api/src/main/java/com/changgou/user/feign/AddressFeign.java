package com.changgou.user.feign;

import com.changgou.user.pojo.Address;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/27 15:53
 */
@FeignClient(value = "user")//声明服务提供者
@RequestMapping("address")
public interface AddressFeign {
    /**
     * 根据用户查询地址列表信息
     *
     * @param
     * @return
     */
    @GetMapping("/user/list")
    public Result<List<Address>> List();
}
