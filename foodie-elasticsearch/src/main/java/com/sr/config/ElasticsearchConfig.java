package com.sr.config;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

/**
 * @author shirui
 * @date 2020/8/6
 */
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
@Getter
@Setter
public class ElasticsearchConfig {

    /**
     * http地址
     */
    private String hosts;

    /**
     * http连接的超时时间
     */
    private int connectTimeout;

    /**
     * socket连接的超时时间
     */
    private int socketTimeout;

    /**
     * 获取链接的超时时间
     */
    private int connectionRequestTimeout;

    /**
     * 最大连接数
     */
    private int maxConnectTotal;

    /**
     * 最大路由连接数
     */
    private int maxConnectPerRoute;

    /**
     * es用户名
     */
    private String username;

    /**
     * es密码
     */
    private String password;

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        // 连接凭证
        // final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        // credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        // 获取地址
        String[] hostArray = hosts.split(",");
        HttpHost[] httpHosts = new HttpHost[hostArray.length];
        for (int i = 0; i < hostArray.length; i++) {
            String[] arr = hostArray[i].split(":");
            httpHosts[i] = new HttpHost(arr[0], Integer.parseInt(arr[1]), "http");
        }

        // 初始化ES客户端的构造器
        RestClientBuilder restClientBuilder = RestClient.builder(httpHosts);

        // 异步请求配置
        restClientBuilder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                // 连接超时时间 默认是-1
                builder.setConnectTimeout(connectTimeout);
                // 连接超时时间 默认是-1
                builder.setSocketTimeout(socketTimeout);
                // 连接超时时间 默认是-1
                builder.setConnectionRequestTimeout(connectionRequestTimeout);
                return builder;
            }
        });

        // 异步httpclient连接数配置
        restClientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                // 最大连接数
                httpAsyncClientBuilder.setMaxConnTotal(maxConnectTotal);
                // 最大路由连接数
                httpAsyncClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
                // 赋予连接凭证
                // httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                return httpAsyncClientBuilder;
            }
        });

        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(restClientBuilder);
        return restHighLevelClient;
    }
}
