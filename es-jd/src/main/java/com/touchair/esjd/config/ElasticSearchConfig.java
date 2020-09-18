package com.touchair.esjd.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * es 配置类
 *
 * @author: bin.wang
 * @date: 2020/9/17 11:20
 */
@Configuration
public class ElasticSearchConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestClientBuilder restClientBuilder;
        RestHighLevelClient restHighLevelClient=new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("127.0.0.1",9200,"http")
                )
        );
        return restHighLevelClient;
    }

}
