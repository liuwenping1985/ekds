package com.seeyon.ekds.engine.reader;

import com.seeyon.ekds.data.descriptor.DataDescriptor;
import com.seeyon.ekds.domain.po.es.EkdsFileContent;

import java.util.List;

/**
 * Created by liuwenping on 2021/6/30.
 */
public interface EsContentReader<T> {

    EkdsFileContent read(DataDescriptor<T> descriptor);




}
