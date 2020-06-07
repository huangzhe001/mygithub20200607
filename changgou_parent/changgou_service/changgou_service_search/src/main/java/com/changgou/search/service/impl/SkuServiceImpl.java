package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/18 17:32
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuEsMapper skuEsMapper;
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 导入sku
     */
    @Override
    public void importSku() {
        Result<List<Sku>> skuList = skuFeign.findByStatus("1");
        List<SkuInfo> skuInfos = JSON.parseArray(JSON.toJSONString(skuList.getData()), SkuInfo.class);
        for (SkuInfo skuInfo : skuInfos) {
            Map<String, Object> specMap = JSON.parseObject(skuInfo.getSpec());
            skuInfo.setSpecMap(specMap);
        }
        skuEsMapper.saveAll(skuInfos);
    }

    /**
     * 关键字搜索
     *
     * @param searchMap：搜索条件
     * @return
     */
    @Override
    public Map search(Map<String, String> searchMap) {
        //查询数据都封装到map里
        Map<String, Object> map = new HashMap<>();
        //构建基本查询条件
        NativeSearchQueryBuilder builder = builderBasicQuery(searchMap);
        //根据查询条件查询商品列表
        searchList(map, builder);
       /* // 跟据查询条件-分组查询商品分类列表
        searchCategoryList(map, builder);
        //根据查询条件-分组查询品牌列表
        searchBrandList(map, builder);
        //根据查询条件-分组查询规格列表
        searchSpecList(map, builder);*/
        //一次分组查询商品的分类，品牌，规格列表，只查询一次
        searchGroup(map, builder);
        return map;
    }

    /**
     * 一次分组查询商品的分类，品牌，规格列表，只查询一次
     *
     * @param map
     * @param builder
     */
    private void searchGroup(Map<String, Object> map, NativeSearchQueryBuilder builder) {
        //1.添加分组查询参数-builder.addAggregation(termsAggregationBuilder)
        builder.addAggregation(AggregationBuilders.terms("group_spec").field("spec.keyword").size(10000));
        builder.addAggregation(AggregationBuilders.terms("group_brand").field("brandName"));
        builder.addAggregation(AggregationBuilders.terms("group_category").field("categoryName"));
        //3.执行搜索-esTemplate.queryForPage(builder.build(), SkuInfo.class)
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);
        //4.获取所有分组查询结果集-page.getAggregations()
        Aggregations aggregations = page.getAggregations();

        //提取分类结果
        //5.提取分组结果数据-stringTerms = aggregations.get(填入刚才查询时的别名)
        StringTerms categoryTerms = aggregations.get("group_category");
        //6.定义分类名字列表-categoryList = new ArrayList<String>()
        List<String> categoryList = new ArrayList<>();
        //7.遍历读取分组查询结果-stringTerms.getBuckets().for
        for (StringTerms.Bucket bucket : categoryTerms.getBuckets()) {
            //7.1获取分类名字，并将分类名字存入到集合中-bucket.getKeyAsString()
            categoryList.add(bucket.getKeyAsString());
        }
        //8.返回分类数据列表-map.put("categoryList", categoryList)
        map.put("categoryList", categoryList);

        //提取品牌结果
        //5.提取分组结果数据-stringTerms = aggregations.get(填入刚才查询时的别名)
        StringTerms brandTerms = aggregations.get("group_brand");
        //6.定义分类名字列表-brandList = new ArrayList<String>()
        List<String> brandList = new ArrayList<>();
        //7.遍历读取分组查询结果-stringTerms.getBuckets().for
        for (StringTerms.Bucket bucket : brandTerms.getBuckets()) {
            //7.1获取分类名字，并将分类名字存入到集合中-bucket.getKeyAsString()
            brandList.add(bucket.getKeyAsString());
        }
        //8.返回分类数据列表-map.put("brandList", brandList)
        map.put("brandList", brandList);

        //5.提取规格结果
        StringTerms stringTerms = aggregations.get("group_spec");
        //6.定义分类名字列表-brandList = new ArrayList<String>()
        List<String> specList = new ArrayList<>();
        //7.遍历读取分组查询结果-stringTerms.getBuckets().for
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //7.1获取分类名字，并将分类名字存入到集合中-bucket.getKeyAsString()
            specList.add(bucket.getKeyAsString());
        }
        //组装规格参数
        //此处用set集合存储value值，可以去重，不同就添加，相同就覆盖
        Map<String, Set<String>> specMap = new HashMap<>();
        Set<String> tempSet = null;
        Map<String, String> tempMap = null;
        for (String spec : specList) {
            //{"电视音响效果":"小影院","电视屏幕尺寸":"20英寸","尺码":"165"}
            //把spec的json数据转换成map
            tempMap = JSON.parseObject(spec, Map.class);
            //循环读取map中的key和value组装到specMap中
            for (String key : tempMap.keySet()) {
                if (specMap.get(key) == null) {
                    //如果specMap中不存在set就直接new一个
                    tempSet = new HashSet<>();
                    //放入到specMap中
                    specMap.put(key, tempSet);
                } else {
                    //存在set,直接从specMap中获取
                    tempSet = specMap.get(key);
                }
                tempSet.add(tempMap.get(key));
            }
        }
        //8.返回分类数据列表-map.put("specMap", specMap)
        map.put("specMap", specMap);
    }

    /**
     * 提取分组聚合结果
     *
     * @param aggregations 聚合结果对象
     * @param group_name   分组域的别名
     * @return 提取的结果集
     */
    private List<String> getGroupResult(Aggregations aggregations, String group_name) {
        //5.提取分组结果数据-stringTerms = aggregations.get(填入刚才查询时的别名)
        StringTerms stringTerms = aggregations.get(group_name);
        //6.定义分类名字列表-brandList = new ArrayList<String>()
        List<String> groupList = new ArrayList<>();
        //7.遍历读取分组查询结果-stringTerms.getBuckets().for
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //7.1获取分类名字，并将分类名字存入到集合中-bucket.getKeyAsString()
            groupList.add(bucket.getKeyAsString());
        }
        return groupList;
    }

    /**
     * 根据查询条件-分组查询规格列表
     *
     * @param map
     * @param builder
     */
   /* private void searchSpecList(Map<String, Object> map, NativeSearchQueryBuilder builder) {
        //1.设置分组域名-termsAggregationBuilder = AggregationBuilders.terms(别名).field(域名);
        // 此处的域如果写成spec,是不支持聚合查询的，elasticsearch底层会帮我们自动生成可聚合的列 spec.keyword
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("group_spec").field("spec.keyword").size(10000);
        //2.添加分组查询参数-builder.addAggregation(termsAggregationBuilder)
        builder.addAggregation(termsAggregationBuilder);
        //3.执行搜索-esTemplate.queryForPage(builder.build(), SkuInfo.class)
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);
        //4.获取所有分组查询结果集-page.getAggregations()
        Aggregations aggregations = page.getAggregations();
        //5.提取分组结果数据-stringTerms = aggregations.get(填入刚才查询时的别名)
        StringTerms stringTerms = aggregations.get("group_spec");
        //6.定义分类名字列表-brandList = new ArrayList<String>()
        List<String> specList = new ArrayList<>();
        //7.遍历读取分组查询结果-stringTerms.getBuckets().for
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //7.1获取分类名字，并将分类名字存入到集合中-bucket.getKeyAsString()
            specList.add(bucket.getKeyAsString());
        }
        //组装规格参数
        //此处用set集合存储value值，可以去重，不同就添加，相同就覆盖
        Map<String, Set<String>> specMap = new HashMap<>();
        Set<String> tempSet = null;
        Map<String, String> tempMap = null;
        for (String spec : specList) {
            //{"电视音响效果":"小影院","电视屏幕尺寸":"20英寸","尺码":"165"}
            //把spec的json数据转换成map
            tempMap = JSON.parseObject(spec, Map.class);
            //循环读取map中的key和value组装到specMap中
            for (String key : tempMap.keySet()) {
                if (specMap.get(key) == null) {
                    //如果specMap中不存在set就直接new一个
                    tempSet = new HashSet<>();
                    //放入到specMap中
                    specMap.put(key, tempSet);
                } else {
                    //存在set,直接从specMap中获取
                    tempSet = specMap.get(key);
                }
                tempSet.add(tempMap.get(key));
            }
        }
        //8.返回分类数据列表-map.put("specMap", specMap)
        map.put("specMap", specMap);
    }
*/
    /**
     * 根据查询条件-分组查询品牌列表
     *
     * @param map
     * @param builder
     */
  /*  private void searchBrandList(Map<String, Object> map, NativeSearchQueryBuilder builder) {
        //1.设置分组域名-termsAggregationBuilder = AggregationBuilders.terms(别名).field(域名);
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("group_brand").field("brandName");
        //2.添加分组查询参数-builder.addAggregation(termsAggregationBuilder)
        builder.addAggregation(termsAggregationBuilder);
        //3.执行搜索-esTemplate.queryForPage(builder.build(), SkuInfo.class)
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);
        //4.获取所有分组查询结果集-page.getAggregations()
        Aggregations aggregations = page.getAggregations();
        //5.提取分组结果数据-stringTerms = aggregations.get(填入刚才查询时的别名)
        StringTerms stringTerms = aggregations.get("group_brand");
        //6.定义分类名字列表-brandList = new ArrayList<String>()
        List<String> brandList = new ArrayList<>();
        //7.遍历读取分组查询结果-stringTerms.getBuckets().for
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //7.1获取分类名字，并将分类名字存入到集合中-bucket.getKeyAsString()
            brandList.add(bucket.getKeyAsString());
        }
        //8.返回分类数据列表-map.put("brandList", brandList)
        map.put("brandList", brandList);

    }
*/

    /**
     * 构建基本查询条件
     *
     * @param searchMap
     * @return
     */
    private NativeSearchQueryBuilder builderBasicQuery(Map<String, String> searchMap) {
        //1、创建查询条件构建器-builder = new NativeSearchQueryBuilder()
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //2、组装查询条件
        //2.1关键字搜索-builder.withQuery(QueryBuilders.matchQuery(域名，内容))
        if (searchMap != null) {
            //用户传入了搜索条件
            BoolQueryBuilder booleanQueryBuilder = QueryBuilders.boolQuery();
            String keywords = searchMap.get("keywords") == null ? "" : searchMap.get("keywords");
            if (StringUtils.isNotEmpty(keywords)) {
                //查询name域
                // builder.withQuery(QueryBuilders.matchQuery("name", keywords));
                booleanQueryBuilder.must(QueryBuilders.matchQuery("name", keywords));
            }

            //分类查询条件
            String category = searchMap.get("category") == null ? "" : searchMap.get("category");
            if (StringUtils.isNotEmpty(category)) {
                //查询category域
                booleanQueryBuilder.must(QueryBuilders.termQuery("categoryName", category));
            }

            //品牌查询条件
            String brand = searchMap.get("brand") == null ? "" : searchMap.get("brand");
            if (StringUtils.isNotEmpty(brand)) {
                //查询brand域
                booleanQueryBuilder.must(QueryBuilders.termQuery("brandName", brand));
            }
            //规格查询条件
            for (String key : searchMap.keySet()) {
                if (key.startsWith("spec_")) {
                    //String value = searchMap.get(key).replace("\\", "");
                    // 以spec_开头的就是规格参数
                    String specField = "specMap." + key.substring(5) + ".keyword";
                    booleanQueryBuilder.must(QueryBuilders.termQuery(specField, searchMap.get(key)));
                }
            }
            //价格区间查询条件
            String price = searchMap.get("price") == null ? "" : searchMap.get("price");
            if (StringUtils.isNotEmpty(price)) {
                //范围匹配查询
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
                //查询price域,500-1000,拆分前台传入的价格区间
                String[] split = price.split("-");
                //先查第一部分价格
                booleanQueryBuilder.must(rangeQueryBuilder.gte(split[0]));
                if (split.length > 1) {
                    //传入的价格区间有最大值
                    booleanQueryBuilder.must(rangeQueryBuilder.lte(split[1]));
                }
            }
            //追加多条件匹配查询
            builder.withQuery(booleanQueryBuilder);

            //当前页，如果没有传参数，默认查第一页，索引为0
            Integer page = searchMap.get("pageNum") == null ? 0 : new Integer(searchMap.get("pageNum")) - 1;
            Integer pageSize = 5;//每页5条数据
            PageRequest pageRequest = PageRequest.of(page, pageSize);
            //设置分页查询
            builder.withPageable(pageRequest);
            //排序
            String sortRule = searchMap.get("sortRule") == null ? "" : searchMap.get("sortRule");
            //排序的域名
            String sortField = searchMap.get("sortField") == null ? "" : searchMap.get("sortField");
            if (StringUtils.isNotEmpty(sortField)) {
                builder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule.toUpperCase())));
            }
        }
        return builder;
    }


    /**
     * 根据查询条件-查询商品列表
     *
     * @param map
     * @param builder
     */
    private void searchList(Map<String, Object> map, NativeSearchQueryBuilder builder) {
        //h1.配置高亮查询信息-hField = new HighlightBuilder.Field()
        HighlightBuilder.Field hField = new HighlightBuilder.Field("name");
        //h1.1:设置高亮域名-在构造函数中设置
        //h1.2：设置高亮前缀-hField.preTags
        hField.preTags("<em style='color:red;'>");
        //h1.3：设置高亮后缀-hField.postTags
        hField.postTags("</em>");
        //h1.4：设置碎片大小-hField.fragmentSize
        hField.fragmentSize(100);
        //h1.5：追加高亮查询信息-builder.withHighlightFields()
        builder.withHighlightFields(hField);
        //3、获取NativeSearchQuery搜索条件对象-builder.build()
        NativeSearchQuery query = builder.build();
        //h2.高亮数据读取-AggregatedPage<SkuInfo> page = esTemplate.queryForPage(query, SkuInfo.class, new SearchResultMapper(){})
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(query, SkuInfo.class, new SearchResultMapper() {
            //h2.1实现mapResults(查询到的结果,数据列表的类型,分页选项)方法
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
            //h2.2 先定义一组查询结果列表-List<T> list = new ArrayList<T>()
                List<T>list=new ArrayList<>();
                //h2.3 遍历查询到的所有高亮数据-response.getHits().for
                for (SearchHit hit : searchResponse.getHits()) {
                    //h2.3.1 先获取当次结果的原始数据(无高亮)-hit.getSourceAsString()
                    String json = hit.getSourceAsString();
                    //h2.3.2 把json串转换为SkuInfo对象-skuInfo = JSON.parseObject()
                    SkuInfo skuInfo = JSON.parseObject(json, SkuInfo.class);
                    //h2.3.3 获取name域的高亮数据-nameHighlight = hit.getHighlightFields().get("name")
                    HighlightField nameHighlight = hit.getHighlightFields().get("name");
                    //h2.3.4 如果高亮数据不为空-读取高亮数据
                    if(nameHighlight!=null){
                        //h2.3.4.1 定义一个StringBuffer用于存储高亮碎片-buffer = new StringBuffer()
                        StringBuffer buffer=new StringBuffer();
                        //h2.3.4.2 循环组装高亮碎片数据- nameHighlight.getFragments().for(追加数据)
                        for (Text fragment : nameHighlight.getFragments()) {
                            buffer.append(fragment);
                            //h2.3.4.3 将非高亮数据替换成高亮数据-skuInfo.setName()
                        }
                        skuInfo.setName(buffer.toString());
                        //h2.3.5 将替换了高亮数据的对象封装到List中-list.add((T) esItem)
                        list.add((T) skuInfo);
                    }
                }
                //h2.4 返回当前方法所需要参数-new AggregatedPageImpl<T>(数据列表，分页选项,总记录数)
                //h2.4 参考new AggregatedPageImpl<T>(list,pageable,response.getHits().getTotalHits())
                return new AggregatedPageImpl<>(list,pageable,searchResponse.getHits().getTotalHits());
            }
        });
        //4.查询数据-esTemplate.queryForPage(条件对象,搜索结果对象)
        // AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(query, SkuInfo.class);
        //5、包装结果并返回
        map.put("rows", page.getContent());
        map.put("totalPages", page.getTotalPages());
        map.put("total", page.getTotalElements());
        //设置当前页
        int pageNum = query.getPageable().getPageNumber();
        map.put("pageNum",pageNum);
        //每页查询记录数
        int pageSize = query.getPageable().getPageSize();
        map.put("pageSize",pageSize);
    }

    /**
     * 跟据查询条件-分组查询商品分类列表
     *
     * @param map     结果集包装
     * @param builder 查询条件构建器
     */
    /*private void searchCategoryList(Map map, NativeSearchQueryBuilder builder) {
        //1.设置分组域名-termsAggregationBuilder = AggregationBuilders.terms(别名).field(域名);
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("group_category").field("categoryName");
        //2.添加分组查询参数-builder.addAggregation(termsAggregationBuilder)
        builder.addAggregation(termsAggregationBuilder);
        //3.执行搜索-esTemplate.queryForPage(builder.build(), SkuInfo.class)
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);
        //4.获取所有分组查询结果集-page.getAggregations()
        Aggregations aggregations = page.getAggregations();
        //5.提取分组结果数据-stringTerms = aggregations.get(填入刚才查询时的别名)
        StringTerms stringTerms = aggregations.get("group_category");
        //6.定义分类名字列表-categoryList = new ArrayList<String>()
        List<String> categoryList = new ArrayList<>();
        //7.遍历读取分组查询结果-stringTerms.getBuckets().for
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //7.1获取分类名字，并将分类名字存入到集合中-bucket.getKeyAsString()
            categoryList.add(bucket.getKeyAsString());
        }
        //8.返回分类数据列表-map.put("categoryList", categoryList)
        map.put("categoryList", categoryList);
    }*/
}
