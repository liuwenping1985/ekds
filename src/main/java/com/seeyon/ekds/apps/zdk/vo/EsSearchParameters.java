package com.seeyon.ekds.apps.zdk.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuwenping on 2021/7/29.
 */
@Data
public class EsSearchParameters {

    private String sysid;

    private String token;

    @JSONField(name = "file_type")
    private String file_type;

    @JSONField(name = "file_source")
    private String file_source;

    @JSONField(name = "key_words")
    private String key_words;

    @JSONField(name = "iso_type")
    private String iso_type;

    @JSONField(name = "iso_status")
    private String iso_status;

    private String counting = "true";

    private String offset;

    private String limit;

    private String contentName = "content,file_name";

    private String page = "1";

    private String pageSize = "10";

    private String count = "0";

    public String formatKeyWords() {
        String keyWords = this.key_words;
        if (keyWords == null) {
            keyWords = "";
        }
        return keyWords.trim().replaceAll(" ", "");
    }

    public Map<String, Integer> page() {
        Map<String, Integer> data = new HashMap<>();
        data.put("from", 0);
        data.put("size", 10);
        if (offset != null && limit != null) {
            int o = Integer.parseInt(offset);
            int l = Integer.parseInt(limit);
            try {
                data.put("from", o);
                data.put("size", l);
                return data;
            } catch (Exception e) {

            }
        }
        try {
            int p = Integer.parseInt(page);
            int ps = Integer.parseInt(pageSize);
            if (p > 0) {
                data.put("from", ((p - 1) * ps));
            }
            data.put("size", ps);
        } catch (Exception e) {

        }

        return data;
    }


}
