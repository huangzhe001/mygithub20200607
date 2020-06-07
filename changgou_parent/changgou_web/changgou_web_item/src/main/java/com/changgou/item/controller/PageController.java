package com.changgou.item.controller;

import com.changgou.item.service.PageService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/22 20:45
 */
@RestController
@RequestMapping("page")
public class PageController {
    @Autowired
    private PageService pageService;

    @GetMapping("createHtml/{spuId}")
    public Result creatPageHtml(@PathVariable Long spuId) {
        pageService.creatPageHtml(spuId);
        return new Result(true, StatusCode.OK, "生成静态页成功");
    }
}
