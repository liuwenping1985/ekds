package com.seeyon.ekds.data.locator;

import com.seeyon.ekds.data.descriptor.DataDescriptor;

/**
 * Created by liuwenping on 2021/6/25.
 */
public interface DataLocator<DATA_TYPE,SC_TYPE> {

    /**
     * 通过数据描述对象获取数据
     *
     * @param dataDescriptor
     */
    DATA_TYPE locateData(DataDescriptor<SC_TYPE> dataDescriptor);

    boolean closeIfNecessary();

    DataDescriptor<SC_TYPE> getDataDescriptor();

}
