package com.seeyon.ekds.data.locator.impl;

import com.seeyon.ekds.apps.zdk.vo.ZdkFileInfo;
import com.seeyon.ekds.data.descriptor.DataDescriptor;
import com.seeyon.ekds.data.locator.DataLocator;
import com.seeyon.ekds.exception.EkdsRuntimeException;

import java.io.*;
import java.net.URL;

/**
 * Created by liuwenping on 2021/6/25.
 *
 * @author liuwenping
 */
public class ZdkFileDataLocator implements DataLocator<InputStream, ZdkFileInfo> {


    private DataDescriptor<ZdkFileInfo> descriptor;

    private boolean needClose = false;

    private InputStream inputStream = null;

    public ZdkFileDataLocator() {

    }

    public ZdkFileDataLocator(DataDescriptor<ZdkFileInfo> dataDescriptor) {
        this.descriptor = dataDescriptor;
    }

    @Override
    public InputStream locateData(DataDescriptor<ZdkFileInfo> dataDescriptor) {
        if (dataDescriptor == null && this.descriptor == null) {
            throw new EkdsRuntimeException("不能解析数据描述符信息:" + dataDescriptor);
        }
        this.descriptor = dataDescriptor;
        ZdkFileInfo info = this.descriptor.getValue();
        File file = new File(info.getLocation());
        try {
            inputStream = new FileInputStream(file);
            needClose = true;
            return inputStream;
        } catch (IOException e) {
            return null;
        }

    }

    @Override
    public boolean closeIfNecessary() {
        if (needClose && inputStream != null) {

            try {
                inputStream.close();
            } catch (IOException e) {

            }
            return true;
        }
        return needClose;
    }

    @Override
    public DataDescriptor<ZdkFileInfo> getDataDescriptor() {
        return this.descriptor;
    }
}
