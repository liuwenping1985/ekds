package com.seeyon.ekds.datasource.es.conversions;

import com.seeyon.ekds.data.vo.GenericDataHolder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Map;

/**
 * Created by liuwenping on 2021/7/2.
 */
@ReadingConverter
public class MapToGenericDataHolder implements Converter<Map<String, Object>, GenericDataHolder<String,Object>> {
    @Override
    public GenericDataHolder<String, Object> convert(Map<String, Object> stringObjectMap) {
        return new GenericDataHolder<>(stringObjectMap);
    }
}
