package com.seeyon.ekds.web;

import lombok.Data;

import java.util.List;

/**
 * Created by liuwenping on 2021/7/16.
 * @author liuwenping
 */
@Data
public class EkdsJSONResponse {

    private String message;
    private String status;
    private boolean success;
    private Object data;
    private List items;


}
