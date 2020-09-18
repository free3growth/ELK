package com.touchair.esapi;

import cn.hutool.json.JSONUtil;
import com.touchair.esapi.pojo.User;
import org.assertj.core.util.Lists;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class EsApiApplicationTests {

    public static final String INDEX_NAME = "java_touchair_index";

    @Resource
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient restHighLevelClient;


    /**
     * 测试创建索引
     *
     * @throws IOException
     */
    @Test
    void testCreateIndex() throws IOException {
        //创建索引的请求
        CreateIndexRequest indexRequest = new CreateIndexRequest("java_touchair_index");
        //客户端执行请求 IndicesClient,请求后获取响应
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(indexRequest, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse.toString());
    }

    /**
     * 测试 获取索引
     *
     * @throws IOException
     */
    @Test
    void testGetIndex() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest("java_touchair_index");
        boolean exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        if (exists) {
            GetIndexResponse getIndexResponse = restHighLevelClient.indices().get(getIndexRequest, RequestOptions.DEFAULT);
            System.out.println(getIndexResponse);
        } else {
            System.out.println("索引不存在");
        }
    }

    /**
     * 测试  删除索引
     *
     * @throws IOException
     */
    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("test2");
        AcknowledgedResponse acknowledgedResponse = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println(acknowledgedResponse.isAcknowledged());
    }


    /**
     * 测试 文档创建
     *
     * @throws IOException
     */
    @Test
    void testAddDocument() throws IOException {
        //创建对象
        User user = new User("java", 23);
        //创建请求
        IndexRequest indexRequest = new IndexRequest("java_touchair_index");

        //规则 put /java_touchair_index/_doc/1
        indexRequest.id("1");
        indexRequest.timeout(TimeValue.timeValueSeconds(1));

        //将数据放入请求
        indexRequest.source(JSONUtil.toJsonPrettyStr(user), XContentType.JSON);

        //客户端发送请求，获取
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse.toString());
        System.out.println(indexResponse.status());
    }

    /**
     * 测试 获取文档
     *
     * @throws IOException
     */
    @Test
    void testGetDocument() throws IOException {
        //判断文档是否存在   get /index/_doc/1
        GetRequest getRequest = new GetRequest(INDEX_NAME, "1");
//        //不获取返回的 _source 的上下文了
//        getRequest.fetchSourceContext(new FetchSourceContext(false));
//        getRequest.storedFields("_none_");
        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        if (exists) {
            GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            System.out.println(getResponse.toString());
            //打印文档内容
            //返回的全部内容和命令行结果一模一样
            System.out.println(getResponse.getSourceAsString());
        } else {
            System.out.println("文档不存在");
        }
    }

    /**
     * 测试 更新文档信息
     *
     * @throws IOException
     */
    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(INDEX_NAME, "1");
        updateRequest.timeout("1s");
        User user = new User("ES搜索引擎", 24);
        updateRequest.doc(JSONUtil.toJsonPrettyStr(user), XContentType.JSON);
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(updateResponse.status());
    }

    /**
     * 测试 删除文档
     *
     * @throws IOException
     */
    @Test
    void testDeleteDocument() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(INDEX_NAME, "1");
        deleteRequest.timeout("1s");
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(deleteResponse.getResult());
    }


    /**
     * 特殊 ，真实项目一般都是批量插入数据
     *
     * @throws IOException
     */
    @Test
    void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        ArrayList<User> userList = Lists.newArrayList();

        userList.add(new User("Java", 11));
        userList.add(new User("javaScript", 12));
        userList.add(new User("Vue", 13));
        userList.add(new User("Mysql", 14));
        userList.add(new User("Docker", 15));
        userList.add(new User("MongoDB", 16));
        userList.add(new User("Redis", 17));
        userList.add(new User("Tomcat", 18));

        for (int i = 0; i < userList.size(); i++) {
            //批量更新和批量删除 只需在这里修改对应的请求即可
            bulkRequest.add(new IndexRequest(INDEX_NAME)
                    .id("" + i + 1)
                    .source(JSONUtil.toJsonPrettyStr(userList.get(i)), XContentType.JSON));

        }
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println((bulkResponse.hasFailures())); //是否失败  返回false 则成功
    }


    /**
     * 查询
     * SearchRequest 搜索请求
     * searchSourceBuilder 条件构造
     * highlighter 高亮
     * matchAllQuery  匹配所有
     * termQuery()    精确查找
     *
     * @throws IOException
     */
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询条件，可以使用QueryBuilders工具 快速查询
        //QueryBuilders.matchAllQuery  匹配所有
        //QueryBuilders.termQuery()    精确查找
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("age", 11);
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        searchSourceBuilder.query(matchAllQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
//        searchSourceBuilder.highlighter();
//        searchSourceBuilder.size();
//        searchSourceBuilder.from();
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        for (SearchHit searchHits : searchResponse.getHits().getHits()) {
            System.out.println(searchHits.getSourceAsMap());
        }
    }

}
