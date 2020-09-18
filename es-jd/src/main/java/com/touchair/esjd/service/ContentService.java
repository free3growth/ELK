package com.touchair.esjd.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.touchair.esjd.pojo.Content;
import com.touchair.esjd.util.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: bin.wang
 * @date: 2020/9/18 13:36
 */
@Service
public class ContentService {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    public static final String INDEX_NAME = "jd_goods";

    /**
     * 解析数据放入es中
     */
    public Boolean parseContent(String keywords) throws Exception {
        List<Content> contents = new HtmlParseUtil().parseJD(keywords);
        //批量插入es
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        for(int i=0;i<contents.size();i++){
            bulkRequest.add(
                    new IndexRequest(INDEX_NAME)
                            .source(JSONUtil.toJsonPrettyStr(contents.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }

    /**
     * 获取这些数据 实现搜索功能
     */
    public List<Map<String, Object>> searchPage(String keywords, int pageNo, int pageSize) throws IOException {
        if(pageNo<=1){
            pageNo=1;
        }
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keywords);
        searchSourceBuilder.query(termQueryBuilder);
        //分页
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.from(pageNo);

        //执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //解析返回结果
        ArrayList<Map<String, Object>> list=new ArrayList<>();
        for (SearchHit documentFields : (searchResponse.getHits().getHits())) {
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            list.add(sourceAsMap);
        }
        return list;
    }

    /**
     * 搜索高亮
     * @param keywords
     * @param pageNo
     * @param pageSize
     * @return
     * @throws IOException
     */
    public List<Map<String, Object>> searchHighLight(String keywords, int pageNo, int pageSize) throws IOException {
        if(pageNo<=1){
            pageNo=1;
        }
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keywords);
        searchSourceBuilder.query(termQueryBuilder);
        //分页
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.from(pageNo);

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //多次出现目标 主需要高亮一个
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        //执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //解析返回结果
        ArrayList<Map<String, Object>> list=new ArrayList<>();
        for (SearchHit hit : (searchResponse.getHits().getHits())) {
            //解析高亮的字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //将原来的字段换为高亮字段
            if (ObjectUtil.isNotNull(title)) {
                Text[] fragments = title.fragments();
                String newTitle = "";
                for (Text text : fragments) {
                    newTitle += text;
                }
                sourceAsMap.put("title", newTitle);
            }
            list.add(sourceAsMap);
        }
        return list;
    }

    }
