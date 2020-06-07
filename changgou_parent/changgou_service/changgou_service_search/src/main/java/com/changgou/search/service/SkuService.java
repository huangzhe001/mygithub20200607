package com.changgou.search.service;

import java.util.Map;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/18 17:30
 */
public interface SkuService {

    /**
     * 导入sku数据
     */
    void importSku();

    /**关键字搜索
     * @param searchMap：搜索条件
     * @return：结果集
     */
    Map search(Map<String,String>searchMap);

}
