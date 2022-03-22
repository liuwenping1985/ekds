package com.seeyon.ekds.data.descriptor;

import lombok.Data;

import java.util.List;

/**
 * Created by liuwenping on 2021/7/23.
 *
 * @Author lwp
 */
@Data
public class DataDescriptor<T> {

    private String name;
    private String type;
    private T value;
    private List<T> items;
    private boolean transientObject;
    private String description;
    private Object extend;
    private Object extend1;
    private Object extend2;
    private Object extend3;
    private Object extend4;

}
