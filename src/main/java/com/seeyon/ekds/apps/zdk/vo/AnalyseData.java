package com.seeyon.ekds.apps.zdk.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import java.util.Map;

/**
 * Created by liuwenping on 2021/7/29.
 */

@Data
public class AnalyseData {
    /**
     * 文档唯一标识（OA端）
     */
    @JSONField(name="doc_mo_id")
    private String docMoId;
    /**
     * 文档名称
     */
    @JSONField(name="doc_name")
    private String docName;

    /**
     * 文档类型 参见mime类型
     */
    @JSONField(name="doc_type")
    private String docType;
    /**
     * 扩展字段，如果需求有变化一般来说也是
     * 在这里进行变更，可按照双方约定进行自定义设置，对于OA属于包装类进行
     */
    @JSONField(name="doc_extend_data")
    private Map docExtendData;


    /**
     * 摘要段
     */
    @JSONField(name="summary")
    private String summary;

    /**
     *摘要段所在位置(格式为类似:位置:单位文档>档案>计划财务部>发文>2021)
     */
    @JSONField(name="doc_position")
    private String docPosition;


    /**
     * 关键词（搜索词）
     */
    @JSONField(name="key_words")
    private String keyWords;

    /**
     * 摘要html版本（待商榷，不一定会实现）
     */
    @JSONField(name="html_summary")
    private  String htmlSummary;

    /**
     * 摘要扩展字段（双方约定）
     */
    @JSONField(name="summary_extend")
    private Map summaryExtend;


}
