package com.seeyon.ekds.data.locator.impl;

import com.seeyon.ekds.data.descriptor.DataDescriptor;
import com.seeyon.ekds.data.locator.DataLocator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwenping on 2021/7/27.
 */
public class BatchURLFileDataLocator implements DataLocator<List<URL>,String> {

    private DataDescriptor<String> dataDescriptor;

    @Override
    public List<URL> locateData(DataDescriptor<String> dataDescriptor) {
        this.dataDescriptor = dataDescriptor;
        List<URL>urlList = new ArrayList<>();

        return null;
    }

    @Override
    public boolean closeIfNecessary() {
        return false;
    }

    @Override
    public DataDescriptor<String> getDataDescriptor() {
        return dataDescriptor;
    }
}
