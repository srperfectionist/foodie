package com.sr.util;

import com.google.common.collect.Lists;
import com.sr.pojo.ElasticEntity;
import com.sr.utils.JSONUtil;
import com.sr.utils.PageGridResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
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
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author shirui
 * @date 2020/8/9
 */
@Component
@Slf4j
public class ElasticsearchUtil {

    private RestHighLevelClient restHighLevelClient;

    @Autowired
    public void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * 创建索引
     * @param indexName
     * @return
     * @throws IOException
     */
    public boolean createIndex(String indexName) throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        return createIndexResponse.isAcknowledged();
    }

    /**
     * 删除索引
     * @param indexName
     * @return
     * @throws IOException
     */
    public boolean deleteIndex(String indexName) throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
        deleteIndexRequest.indicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN);
        return restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT).isAcknowledged();
    }

    /**
     * 判断索引是否存在
     * @param indexName
     * @return
     * @throws IOException
     */
    public boolean existIndex(String indexName) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        getIndexRequest.humanReadable(true);
        return restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
    }

    /**
     * 创建文档
     * @param indexName
     * @param obj
     * @return
     * @throws IOException
     */
    public IndexResponse createDoc(String indexName, Object obj) throws IOException {
        if (existIndex(indexName)){
            log.info("文档不存在:{}", indexName);
        }
        Long count = countDoc(indexName);
        IndexRequest indexRequest = new IndexRequest(indexName);
        indexRequest.id(String.valueOf(count + 1));
        indexRequest.source(JSONUtil.objToString(obj), XContentType.JSON);
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        return indexResponse;
    }

    /**
     * 批量创建文档
     * @param indexName
     * @param list
     * @return
     * @throws IOException
     */
    public <T> boolean createDocBatch(String indexName, List<T> list) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (int i = 0; i < list.size(); i++) {
            bulkRequest.add(new IndexRequest(indexName).id(String.valueOf(i)).source(JSONUtil.objToString(list.get(i)), XContentType.JSON));;
        }
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        boolean successOrFail = Optional.ofNullable(bulkResponse).map(item -> {
            for (BulkItemResponse bulkItemResponse : item.getItems()) {
                DocWriteResponse docWriteResponse = bulkItemResponse.getResponse();
                log.info("单条返回结果：{}", docWriteResponse);
                if (bulkItemResponse.isFailed()) {
                    log.error("es批量插入失败，返回错误信息：{}", bulkItemResponse.getFailureMessage());
                    return false;
                }
            }
            return true;
        }).orElse(true);
        return successOrFail;
    }

    /**
     * 获取文档个数
     * @param indexName
     * @return
     * @throws IOException
     */
    public Long countDoc(String indexName) throws IOException {
        CountRequest request = new CountRequest(indexName);
        CountResponse response = restHighLevelClient.count(request, RequestOptions.DEFAULT);
        return response.getCount();
    }

    /**
     * 根据Id获取文档信息
     * @param indexName
     * @param docId
     * @return
     * @throws IOException
     */
    public String searchById(String indexName, String docId) throws IOException {
        GetRequest getRequest = new GetRequest(indexName, docId);
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        String result = getResponse.getSourceAsString();
        return result;
    }

    /**
     * 分页查询，高亮显示
     * @param indexName
     * @param fieldName
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     * @throws IOException
     */
    public PageGridResult searchPageHighLight(String indexName, String fieldName, String keyword, Integer page, Integer pageSize) throws IOException {
        if (!existIndex(indexName)){
            log.info("{}索引不存在", indexName);
            return PageGridResult.builder().build();
        }
        if (pageSize == 0){
            pageSize = 10;
        }
        // 构建检索条件
        SearchRequest searchRequest = new SearchRequest(indexName);
        // 结果高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        // 如果该属性中有多个关键字 则都高亮
        highlightBuilder.requireFieldMatch(true);
        highlightBuilder.field(fieldName);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        // 指定检索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(page);
        sourceBuilder.size(pageSize);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        sourceBuilder.query(QueryBuilders.multiMatchQuery(keyword,fieldName));
        sourceBuilder.highlighter(highlightBuilder);
        // 开始检索
        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = response.getHits().getHits();
        TotalHits totalHits = response.getHits().getTotalHits();
        List<Map<String, Object>> resultList = Lists.newArrayList();
        for (SearchHit hit : hits) {
            // 解析高亮字段
            // 获取当前命中的对象的高亮的字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlightField = highlightFields.get(fieldName);
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if (highlightField != null){
                // 获取该高亮字段的高亮信息
                Text[] fragments = highlightField.getFragments();
                StringBuilder stringBuilder = new StringBuilder();
                // 将前缀、关键词、后缀进行拼接
                for (Text fragment : fragments) {
                    stringBuilder.append(fragment);
                }
                // 将高亮后的值替换掉旧值
                sourceAsMap.put(fieldName, stringBuilder.toString());
            }
            resultList.add(sourceAsMap);
        }
        return PageGridResult.builder().page(page).total(resultList.size()).records(Long.valueOf(response.getHits().getTotalHits().value)).rows(resultList).build();
    }

    /**
     * 分页查询，高亮显示
     * @param indexName
     * @param fieldName
     * @param keyword
     * @param page
     * @param pageSize
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> PageGridResult searchPageHighLight(String indexName, String fieldName, String keyword, Integer page, Integer pageSize, String sortName, String sort, Class<T> clazz) throws IOException {
        if (!existIndex(indexName)){
            log.info("{}索引不存在", indexName);
            return PageGridResult.builder().build();
        }
        if (pageSize == 0){
            pageSize = 10;
        }
        // 构建检索条件
        SearchRequest searchRequest = new SearchRequest(indexName);
        // 结果高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        // 如果该属性中有多个关键字 则都高亮
        highlightBuilder.requireFieldMatch(true);
        highlightBuilder.field(fieldName);
        // 自定义样式 返回em
//        highlightBuilder.preTags("<span style='color:red'>");
//        highlightBuilder.postTags("</span>");
        // 指定检索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(page * pageSize);
        sourceBuilder.size(pageSize);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        sourceBuilder.query(QueryBuilders.multiMatchQuery(keyword,fieldName));
        if (!StringUtils.equals("", sortName)){
            sourceBuilder.sort(sortName, StringUtils.equalsIgnoreCase(sort, SortOrder.ASC.toString()) ? SortOrder.ASC : SortOrder.DESC);
        }
        sourceBuilder.highlighter(highlightBuilder);
        // 开始检索
        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = response.getHits().getHits();

        List<T> resultList = Lists.newArrayList();
        for (SearchHit hit : hits) {
            // 解析高亮字段
            // 获取当前命中的对象的高亮的字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlightField = highlightFields.get(fieldName);
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            StringBuilder stringBuilder = new StringBuilder();
            if (highlightField != null){
                // 获取该高亮字段的高亮信息
                Text[] fragments = highlightField.getFragments();
                // 将前缀、关键词、后缀进行拼接
                for (Text fragment : fragments) {
                    stringBuilder.append(fragment);
                }

                // 将高亮后的值替换掉旧值
                sourceAsMap.put(fieldName, stringBuilder.toString());
                String json = JSONUtil.objToString(sourceAsMap);
                T t = JSONUtil.stringToObject(json, clazz);
                resultList.add(t);
                stringBuilder.setLength(0);
            }
        }

        PageGridResult pageGridResult = PageGridResult.builder().page(page + 1).total(resultList.size()).records(Long.valueOf(response.getHits().getTotalHits().value)).rows(resultList).build();
        return pageGridResult;
    }

    /**
     * 分页查询
     * @param indexName
     * @param fieldName
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> List<T> searchPage(String indexName, String fieldName, String keyword, Integer pageNum, Integer pageSize, Class<T> clazz) throws IOException {
        if (!existIndex(indexName)){
            log.info("{}索引不存在", indexName);
            return Lists.newArrayList();
        }
        if (pageSize == 0){
            pageSize = 10;
        }
        // 构建检索条件
        SearchRequest searchRequest = new SearchRequest(indexName);
        // 指定检索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(pageNum);
        sourceBuilder.size(pageSize);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        sourceBuilder.query(QueryBuilders.multiMatchQuery(keyword,fieldName));
        // 开始检索
        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = response.getHits().getHits();

        List<T> resultList = Lists.newArrayList();
        for (SearchHit hit : hits) {
            String value = hit.getSourceAsString();
            T t = JSONUtil.stringToObject(value, clazz);
            resultList.add(t);
        }
        return resultList;
    }

    /**
     * 根据json类型更新文档
     * @param indexName
     * @param docId
     * @param elasticEntity
     * @return
     * @throws IOException
     */
    public boolean updateDoc(String indexName, String docId, ElasticEntity elasticEntity) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(indexName, docId);
        updateRequest.doc(JSONUtil.objToString(elasticEntity.getData()), XContentType.JSON);
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        if (updateResponse.getResult() == DocWriteResponse.Result.CREATED){
            return true;
        }else if(updateResponse.getResult() == DocWriteResponse.Result.UPDATED){
            return true;
        }
        return false;
    }

    /**
     * 删除文档
     * @param indexName
     * @param docId
     * @return
     * @throws IOException
     */
    public boolean deleteDoc(String indexName, String docId) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(indexName, docId);
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        ReplicationResponse.ShardInfo shardInfo = deleteResponse.getShardInfo();
        if(deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND){
            return false;
        }
        if (shardInfo.getFailed() > 0){
            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                String reason = failure.reason();
                log.error("es 删除文档失败原因：", reason);
            }
            return false;
        }
        return true;
    }
}