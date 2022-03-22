package com.seeyon.ekds.data.locator.impl;

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
public class InputStreamDataLocator implements DataLocator<InputStream, String> {


    private DataDescriptor<String> descriptor;

    private boolean needClose = false;

    private InputStream inputStream = null;

    public InputStreamDataLocator() {

    }

    public InputStreamDataLocator(DataDescriptor<String> dataDescriptor) {
        this.descriptor = dataDescriptor;
    }

    @Override
    public InputStream locateData(DataDescriptor<String> dataDescriptor) {
        if (dataDescriptor == null && this.descriptor == null) {
            throw new EkdsRuntimeException("不能解析数据描述符信息:" + dataDescriptor);
        }

        this.descriptor = dataDescriptor;

        String type = this.descriptor.getType();

        switch (type) {
            case "file": {
                File file = new File(this.descriptor.getValue());
                try {
                    inputStream = new FileInputStream(file);
                } catch (IOException e) {
                    return null;
                }
                needClose = true;
                break;
            }
            case "url": {
                try {
                    URL url = new URL(this.descriptor.getValue());
                    inputStream = url.openConnection().getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            }
            case "string": {
                inputStream = new ByteArrayInputStream(this.descriptor.getValue().getBytes());
                break;
            }


            default:
                throw new EkdsRuntimeException("不能解析数据描述符[Type]:" + dataDescriptor.getType());

        }
        return inputStream;
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
    public DataDescriptor getDataDescriptor() {
        return this.descriptor;
    }
}
