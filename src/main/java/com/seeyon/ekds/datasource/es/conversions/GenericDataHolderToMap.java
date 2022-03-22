package com.seeyon.ekds.datasource.es.conversions;


import com.seeyon.ekds.data.vo.GenericDataHolder;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Map;

/**
 * just a toy 玩具类，单存验证自定义转换
 * Created by liuwenping on 2021/7/2.
 */
@WritingConverter
public class GenericDataHolderToMap implements Converter<GenericDataHolder, Map<String, Object>> {

    @Override
    public Map<String, Object> convert(GenericDataHolder genericDataHolder) {

        return genericDataHolder;
    }
}
