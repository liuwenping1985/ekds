package com.seeyon.ekds.data.locator.impl;

import com.seeyon.ekds.data.descriptor.DataDescriptor;
import com.seeyon.ekds.data.locator.DataLocator;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Created by liuwenping on 2021/7/27.
 */
public class BatchFileDataLocator implements DataLocator<List<File>,String> {
    @Override
    public List<File> locateData(DataDescriptor<String> dataDescriptor) {
        return null;
    }

    @Override
    public boolean closeIfNecessary() {
        return false;
    }

    @Override
    public DataDescriptor<String> getDataDescriptor() {
        return null;
    }
}
