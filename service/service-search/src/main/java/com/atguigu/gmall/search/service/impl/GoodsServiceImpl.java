package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.model.list.SearchAttr;
import com.google.common.collect.Lists;
import com.atguigu.gmall.model.vo.search.OrderMapVo;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.service.GoodsService;
import org.apache.lucene.search.join.ScoreMode;
import org.bouncycastle.math.raw.Nat;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    GoodsRepository goodsRepository;
    @Autowired
    ElasticsearchRestTemplate restTemplate;

    /**
     * 保存es
     *
     * @param goods
     */
    @Override
    public void saveGoods(Goods goods) {
        goodsRepository.save(goods);
    }

    /**
     * 删除es
     *
     * @param skuId
     */
    @Override
    public void deleteGoods(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    /**
     * 去es检索商品
     *
     * @param paramVo
     * @return
     */
    @Override
    public SearchResponseVo search(SearchParamVo paramVo) {
        //1.动态构建出搜索条件
        Query query = buildQueryDsl(paramVo);

        //2.搜索
        SearchHits<Goods> goods = restTemplate
                .search(query,
                        Goods.class,
                        IndexCoordinates.of("goods"));
        //3.转换为指定的类型
        SearchResponseVo responseVo = buildSearchResponseResult(goods, paramVo);


        return responseVo;
    }

    /**
     * 根据检索记录构建响应结果
     *
     * @param goods
     * @param paramVo
     * @return
     */
    private SearchResponseVo buildSearchResponseResult(SearchHits<Goods> goods, SearchParamVo paramVo) {
        SearchResponseVo vo = new SearchResponseVo();
        //1. 当时检索前传来的参数
        vo.setSearchParam(paramVo);
        //2.构建品牌面包屑
        if (!StringUtils.isEmpty(paramVo.getTrademark())) {
            vo.setTrademarkParam("品牌" + paramVo.getTrademark().split(":")[1]);
        }
        //3.平台属性面包屑
        if (paramVo.getProps() != null && paramVo.getProps().length > 0) {
            List<SearchAttr> propsParamList = new ArrayList<>();
            for (String prop : paramVo.getProps()) {
                String[] split = prop.split(":");
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(Long.parseLong(split[0]));
                searchAttr.setAttrValue(split[1]);
                searchAttr.setAttrName(split[2]);
                propsParamList.add(searchAttr);
            }
            vo.setPropsParamList(propsParamList);
        }
        //4. TODO 所有品牌列表，需要聚合es分析
        vo.setTrademarkList(Lists.newArrayList());
        //5. TODO 所有属性列表  需要ES聚合分析
        vo.setAttrsList(Lists.newArrayList());

        //为了回显
        //6. 返回排序信息  order=1:desc
        if (!StringUtils.isEmpty(paramVo.getOrder())) {
            String order = paramVo.getOrder();
            OrderMapVo mapVo = new OrderMapVo();
            mapVo.setType(order.split(":")[0]);
            mapVo.setSort(order.split(":")[1]);
            vo.setOrderMap(new OrderMapVo());
        }

        //7.所有搜索到的商品列表
        List<Goods> goodsList = new ArrayList<>();
        List<SearchHit<Goods>> hits = goods.getSearchHits();
        for (SearchHit<Goods> hit : hits) {
            //这条命中记录的商品
            Goods content = hit.getContent();
            //如果模糊搜索到了高亮
            if (!StringUtils.isEmpty(paramVo.getKeyword())){
                String highlightTitle = hit.getHighlightField("title").get(0);
                //设置高亮标题
                content.setTitle(highlightTitle);
            }
            goodsList.add(content);
        }
        vo.setGoodsList(goodsList);
        //8.页码
        vo.setPageNo(paramVo.getPageNo());
        //9.总页码
        long totalHits = goods.getTotalHits();
        long ps = totalHits%SysRedisConst.SEARCH_PAGE_SIZE==0?
                totalHits/SysRedisConst.SEARCH_PAGE_SIZE:
                (totalHits/SysRedisConst.SEARCH_PAGE_SIZE+1);
        vo.setTotalPages(new Integer(ps+""));
        //老链接
        String url = makeUrlParam(paramVo);
        vo.setUrlParam(url);

        return vo;
    }

    /**
     * 制造老链接
     * @param paramVo
     * @return
     */
    private String makeUrlParam(SearchParamVo paramVo) {
        StringBuilder builder = new StringBuilder("list.html?");
        if (paramVo.getCategory1Id()!=null){
            builder.append("&category1Id="+paramVo.getCategory1Id());
        }
        if (paramVo.getCategory2Id()!=null){
            builder.append("&category2Id="+paramVo.getCategory2Id());
        }
        if (paramVo.getCategory3Id()!=null){
            builder.append("&category3Id="+paramVo.getCategory3Id());
        }
        //2、拼关键字
        if(!StringUtils.isEmpty(paramVo.getKeyword())){
            builder.append("&keyword="+paramVo.getKeyword());
        }

        //3、拼品牌
        if(!StringUtils.isEmpty(paramVo.getTrademark())){
            builder.append("&trademark="+paramVo.getTrademark());
        }

        //4、拼属性
        if(paramVo.getProps()!=null && paramVo.getProps().length >0){
            for (String prop : paramVo.getProps()) {
                //props=23:8G:运行内存
                builder.append("&props="+prop);
            }
        }

//        //5、拼排序
//        builder.append("&order="+paramVo.getOrder());
//
//        //6、拼页码
//        builder.append("&pageNo="+paramVo.getPageNo());


        //拿到最终字符串
        String url = builder.toString();
        return url;
    }

    /**
     * 根据前端传来的数据构建检索条件
     *
     * @param paramVo
     * @return
     */
    private Query buildQueryDsl(SearchParamVo paramVo) {
        //1.准备bool查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //2.给bool中准备must
        //2.1 前端传入了category1Id
        if (paramVo.getCategory1Id() != null) {
            boolQuery
                    .must(QueryBuilders
                            .termQuery("getCategory1Id",
                                    paramVo.getCategory1Id()));
        }
        if (paramVo.getCategory2Id() != null) {
            boolQuery.must(QueryBuilders
                    .termQuery("getCategory2Id",
                            paramVo.getCategory2Id()));
        }
        if (paramVo.getCategory3Id() != null) {
            boolQuery.must(QueryBuilders
                    .termQuery("getCategory3Id",
                            paramVo.getCategory3Id()));
        }
        //2.2 前端传了 keyword
        if (!StringUtils.isEmpty(paramVo.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("title", paramVo.getKeyword()));
        }
        //2.3 前端穿了品牌
        if (!StringUtils.isEmpty(paramVo.getTrademark())) {
            //得到品牌Id
            long tmId = Long.parseLong(paramVo.getTrademark().split(":")[0]);
            boolQuery.must(QueryBuilders.termQuery("tmId", tmId));
        }

        //2.4 如果前端传入了属性
        String[] props = paramVo.getProps();
        if (props != null && props.length > 0) {
            for (String prop : props) {
                //得到属性id和值
                String[] split = prop.split(":");
                long attrId = Long.parseLong(split[0]);
                String attrValue = split[1];
                //构造boolQuery
                BoolQueryBuilder nestedBool = QueryBuilders.boolQuery();
                nestedBool.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedBool.must(QueryBuilders.termQuery("attrs.attrValue", attrValue));

                NestedQueryBuilder nestedQuery =
                        QueryBuilders.nestedQuery("attrs", nestedBool, ScoreMode.None);

                //给最大的boolQuery中放 嵌入式nestedBool
                boolQuery.must(nestedQuery);

            }

        }

        //准备一个原生检索条件【原生dsl】
        NativeSearchQuery query = new NativeSearchQuery(boolQuery);
        //2.5 前端传入了排序
        if (!StringUtils.isEmpty(paramVo.getOrder())) {
            String order = paramVo.getOrder();
            String[] split = order.split(":");
            //分析排序用哪个字段
            String orderField = "hotScore";
            switch (split[0]) {
                case "1":
                    orderField = "hotScore";
                    break;
                case "2":
                    orderField = "price";
                    break;
                case "3":
                    orderField = "createTime";
                    break;
                default:
                    orderField = "hotScore";
            }

            //排序方式
            Sort sort = Sort.by(orderField);
            if (split[1].equals("asc")) {
                sort = sort.ascending();
            } else {
                sort = sort.descending();
            }
            query.addSort(sort);

            //2.6 前端传了页码
            if (paramVo.getPageNo() != null && paramVo.getPageNo() > 0) {
                //spring底层是从0开始 自己要计算前端页码-1
                PageRequest request = PageRequest.of(paramVo.getPageNo() - 1, SysRedisConst.SEARCH_PAGE_SIZE);
                query.setPageable(request);
                //===============================排序分页结束=============
                //2.7
                if (!StringUtils.isEmpty(paramVo.getKeyword())) {
                    HighlightBuilder highlightBuilder = new HighlightBuilder();
                    highlightBuilder.field("title")
                            .preTags("<span style = 'color:red'>")
                            .postTags("</span>");
                    HighlightQuery highlightQuery = new HighlightQuery(highlightBuilder);
                    query.setHighlightQuery(highlightQuery);
                }
                //============模糊查询高亮结束===========================

                //TODO ============聚合分析上面的DSL检索到的所有商品中涉及了多少品牌和多少中平台属性


            }


        }
        return query;

    }


}
