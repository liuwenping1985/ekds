package com.seeyon.ekds.data.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuwenping on 2021/6/30.
 */
public class GenericDataHolder<K, VAL> extends HashMap<K, VAL> {

    public GenericDataHolder<K, VAL> set(K k, VAL val) {
        this.put(k, val);
        return this;
    }

    public boolean setIfNotExist(K k, VAL val) {
        VAL valOld = this.get(k);
        if (valOld != null) {
            return false;
        }
        set(k, val);
        return true;
    }
    public GenericDataHolder (){

    }
    public GenericDataHolder (Map<K,VAL> data){
        super(data);
    }

}
