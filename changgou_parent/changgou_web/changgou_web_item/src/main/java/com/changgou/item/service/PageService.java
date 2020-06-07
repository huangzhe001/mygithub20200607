package com.changgou.item.service;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/22 20:35
 */
public interface PageService {

    /**根据商品id生成静态页
     * @param spuId
     */
    public void creatPageHtml(Long spuId);
}
