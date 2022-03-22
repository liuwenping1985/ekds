package com.seeyon.ekds.dao.es.repository;

import com.seeyon.ekds.domain.po.es.EkdsFileContent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Created by liuwenping on 2021/7/16.
 */
@Repository
public interface FileContentPerPageRepository extends ElasticsearchRepository<EkdsFileContent, String> {
    List<EkdsFileContent> findAllByContentContains(String keyWord);
    List<EkdsFileContent> findAllByContentContainsAndIdNotIn(String keyWord, Collection<String> ids);
    List<EkdsFileContent> findAllByContentLike(String keyWord);
    int countAllByContentLike(String keyWord);
    List<EkdsFileContent>  findByContentLike(String keyWord,Pageable pageable);
    List<EkdsFileContent> findByContentLikeAndIdNotIn(String keyWord, Collection<String> ids, Pageable pageable);

    List<EkdsFileContent> findDistinctByContentContainsOrFileNameContains(String words,String words2,Pageable pageable);

    int countDistinctByContentContainsOrFileNameContains(String keyWord,String words2);

    /**/
    int countDistinctByContentContainsOrFileNameContainsAndContentTypeEquals(String contentLikeString,String fileNameLikeString,String contentType);

    List<EkdsFileContent> findDistinctByContentContainsOrFileNameContainsAndContentTypeEquals(String contentLikeString,String fileNameLikeString,String contentType,Pageable pageable);

    int countDistinctByContentLikeOrFileNameLike(String contentLikeString,String fileNameLikeString);

    List<EkdsFileContent> findDistinctByContentLikeOrFileNameLike(String contentLikeString,String fileNameLikeString,Pageable pageable);


}
