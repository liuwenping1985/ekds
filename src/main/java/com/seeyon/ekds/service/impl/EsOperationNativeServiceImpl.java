package com.seeyon.ekds.service.impl;

import com.seeyon.ekds.data.vo.GenericDataHolder;
import com.seeyon.ekds.exception.EkdsRuntimeException;
import com.seeyon.ekds.service.EsOperationNativeService;
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
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * Created by liuwenping on 2021/10/21.
 *
 * @author liuwenping
 */
@Service("esOperationNativeService")
public class EsOperationNativeServiceImpl implements EsOperationNativeService {

    private static final Logger log = LoggerFactory.getLogger(EsOperationNativeService.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public IndexResponse createIndex(String indexName, String id, Map data) {
        IndexRequest indexRequest = new IndexRequest(indexName);
        indexRequest.id(id);
        indexRequest.source(data);
        return createIndex(indexRequest);
    }

    @Override
    public void createIndexAsync(String indexName, String documentName, Map data, ActionListener<IndexResponse> listener) {
        IndexRequest indexRequest = new IndexRequest(indexName);
        indexRequest.id(documentName);
        indexRequest.source(data);
        createIndexAsync(indexRequest, listener);
    }

    @Override
    public GetResponse getData(String indexName, String documentName) {

        GetRequest getRequest = new GetRequest(indexName, documentName);
        return getData(getRequest);
    }

    @Override
    public boolean existCheck(String indexName, String documentName) {
        GetRequest getRequest = new GetRequest(indexName, documentName);
        return existCheck(getRequest);
    }

    @Override
    public void getDataAsync(String indexName, String documentName, ActionListener<GetResponse> listener) {
        GetRequest getRequest = new GetRequest(indexName, documentName);
        getDataAsync(getRequest, listener);
    }

    @Override
    public DeleteResponse deleteData(String indexName, String documentName) {
        DeleteRequest deleteRequest = new DeleteRequest(indexName, documentName);
        return deleteData(deleteRequest);

    }

    @Override
    public void deleteDataAsync(String indexName, String documentName, ActionListener<DeleteResponse> listener) {
        DeleteRequest deleteRequest = new DeleteRequest(indexName, documentName);
        deleteDataAsync(deleteRequest, listener);
    }

    @Override
    public UpdateResponse updateData(String indexName, String documentName, Map data) {

        UpdateRequest updateRequest = new UpdateRequest(indexName, documentName);
        updateRequest.doc(data);
        return updateData(updateRequest);
    }

    @Override
    public void updateDataAsync(String indexName, String documentName, Map data, ActionListener<UpdateResponse> listener) {
        UpdateRequest updateRequest = new UpdateRequest(indexName, documentName);
        updateRequest.doc(data);
        updateDataAsync(updateRequest, listener);
    }

    @Override
    public SearchResponse search(GenericDataHolder searchParams) {
        return null;
    }

    @Override
    public void searchAsync(GenericDataHolder searchParams, ActionListener<SearchResponse> listener) {

    }

    @Override
    public IndexResponse createIndex(IndexRequest request) {
        try {
            return restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("create index error!", e);
            throw new EkdsRuntimeException(e);
        }
    }

    @Override
    public void createIndexAsync(IndexRequest indexRequest, ActionListener<IndexResponse> listener) {
        try {
            restHighLevelClient.indexAsync(indexRequest, RequestOptions.DEFAULT, listener);
        } catch (Exception e) {
            log.error("create index error!", e);
            throw new EkdsRuntimeException(e);
        }
    }

    @Override
    public GetResponse getData(GetRequest request) {
        try {
            GetResponse getResponse = restHighLevelClient.get(request, RequestOptions.DEFAULT);
            return getResponse;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new EkdsRuntimeException(e);
        }
    }

    @Override
    public boolean existCheck(GetRequest request) {
        try {
            return restHighLevelClient.exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new EkdsRuntimeException(e);
        }
    }

    @Override
    public void getDataAsync(GetRequest getRequest, ActionListener<GetResponse> listener) {
        try {
            restHighLevelClient.getAsync(getRequest, RequestOptions.DEFAULT, listener);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EkdsRuntimeException(e);
        }
    }

    @Override
    public void existCheckAsync(GetRequest request, ActionListener<Boolean> listener) {
        try {
             restHighLevelClient.existsAsync(request, RequestOptions.DEFAULT,listener);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EkdsRuntimeException(e);
        }
    }

    @Override
    public DeleteResponse deleteData(DeleteRequest deleteRequest) {
        try {
            return restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new EkdsRuntimeException(e);
        }
    }

    @Override
    public void deleteDataAsync(DeleteRequest deleteRequest, ActionListener<DeleteResponse> listener) {
        try {
            restHighLevelClient.deleteAsync(deleteRequest, RequestOptions.DEFAULT, listener);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EkdsRuntimeException(e);
        }
    }

    @Override
    public UpdateResponse updateData(UpdateRequest updateRequest) {

        try {
            return restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EkdsRuntimeException(e);
        }

    }

    @Override
    public void updateDataAsync(UpdateRequest updateRequest, ActionListener<UpdateResponse> listener) {

        try {
            restHighLevelClient.updateAsync(updateRequest, RequestOptions.DEFAULT, listener);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EkdsRuntimeException(e);
        }
    }

    @Override
    public SearchResponse search(SearchRequest searchRequest) {
        try {

            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EkdsRuntimeException(e);
        }
    }

    @Override
    public void searchAsync(SearchRequest searchRequest, ActionListener<SearchResponse> listener) {
        try {

            restHighLevelClient.searchAsync(searchRequest, RequestOptions.DEFAULT, listener);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EkdsRuntimeException(e);
        }
    }


}
