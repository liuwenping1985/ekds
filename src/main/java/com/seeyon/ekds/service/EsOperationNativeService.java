package com.seeyon.ekds.service;

import com.seeyon.ekds.data.vo.GenericDataHolder;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Response;

import java.util.Map;

/**
 * 10月份重构，使用highlevelclient操作
 *
 * @author liuwenping
 *         <p>
 *         Created by liuwenping on 2021/10/21.
 */
public interface EsOperationNativeService {
    /**
     * 创建索引存储
     *
     * @param indexName
     * @param documentName
     * @param data
     * @return
     */
    IndexResponse createIndex(String indexName, String documentName, Map data);

    /**
     * 异步创建索引
     * @param indexName
     * @param documentName
     * @param data
     * @param listener
     */
    void createIndexAsync(String indexName, String documentName, Map data, ActionListener<IndexResponse> listener);

    /**
     * 获取数据
     * @param indexName
     * @param documentName
     * @return
     */
    GetResponse getData(String indexName, String documentName);

    boolean existCheck(String indexName, String documentName);

    /**
     * 异步获取数据
     * @param indexName
     * @param documentName
     * @param listener
     */
    void getDataAsync(String indexName, String documentName, ActionListener<GetResponse> listener);

    DeleteResponse deleteData(String indexName, String documentName);

    void deleteDataAsync(String indexName, String documentName, ActionListener<DeleteResponse> listener);

    UpdateResponse updateData(String indexName, String documentName, Map data);

    void updateDataAsync(String indexName, String documentName, Map data, ActionListener<UpdateResponse> listener);

    SearchResponse search(GenericDataHolder searchParams);

    void searchAsync(GenericDataHolder searchParams, ActionListener<SearchResponse> listener);

    IndexResponse createIndex(IndexRequest request);

    void createIndexAsync(IndexRequest request, ActionListener<IndexResponse> listener);

    GetResponse getData(GetRequest request);

    boolean existCheck(GetRequest request);

    void getDataAsync(GetRequest request, ActionListener<GetResponse> listener);

    void existCheckAsync(GetRequest request, ActionListener<Boolean> listener);

    DeleteResponse deleteData(DeleteRequest deleteRequest);

    void deleteDataAsync(DeleteRequest deleteRequest, ActionListener<DeleteResponse> listener);

    UpdateResponse updateData(UpdateRequest updateRequest);

    void updateDataAsync(UpdateRequest updateRequest, ActionListener<UpdateResponse> listener);

    SearchResponse search(SearchRequest searchRequest);

    void searchAsync(SearchRequest searchRequest, ActionListener<SearchResponse> listener);



}
