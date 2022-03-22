package com.seeyon.ekds.engine.parser.impl;

import com.seeyon.ekds.engine.parser.EsParser;
import com.seeyon.ekds.util.EkdsUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by liuwenping on 2021/6/25.
 *
 * @Author liuwenping
 */
@Component("esInsParser")
public class InputStreamParser implements EsParser<InputStream, Map> {

    @Override
    public Map parse(InputStream data) {
        if (data == null) {
            throw new RuntimeException(" data not presented！");
        }
        byte[] buffer = new byte[4096];
        int len = -1;
        try {
            byte[] header = null;
            while ((len = data.read(buffer)) > 0) {
                if (len > 6 && header == null) {
                    header = new byte[6];
                    System.arraycopy(buffer, 0, header, 0, 6);
                }
                String fileType = EkdsUtil.getFileType(header);
                if ("unKnown".equals(fileType)) {
                    break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
