package com.seeyon.ekds.util;

import com.seeyon.ekds.web.EkdsJSONResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * Created by liuwenping on 2021/7/16.
 */
public class WebUtil {

    public static EkdsJSONResponse responseSuccessWhithData(Object data){

        EkdsJSONResponse ekdsJSONResponse = new EkdsJSONResponse();
        ekdsJSONResponse.setData(data);
        ekdsJSONResponse.setMessage("success");
        ekdsJSONResponse.setStatus("200");
        ekdsJSONResponse.setSuccess(true);
        return ekdsJSONResponse;

    }
    public static EkdsJSONResponse responseSuccessWhithItems(List items){
        EkdsJSONResponse ekdsJSONResponse = new EkdsJSONResponse();
        ekdsJSONResponse.setItems(items);
        ekdsJSONResponse.setMessage("success");
        ekdsJSONResponse.setStatus("200");
        ekdsJSONResponse.setSuccess(true);
        return ekdsJSONResponse;

    }
    public static EkdsJSONResponse responseFailWhithDataAndMessage(Object data, String message){
        EkdsJSONResponse ekdsJSONResponse = new EkdsJSONResponse();
        ekdsJSONResponse.setData(data);
        ekdsJSONResponse.setMessage("message");
        ekdsJSONResponse.setStatus("200");
        ekdsJSONResponse.setSuccess(true);
        return ekdsJSONResponse;

    }
    public static EkdsJSONResponse responseFailWhithMessage(String message){
        EkdsJSONResponse ekdsJSONResponse = new EkdsJSONResponse();
        ekdsJSONResponse.setMessage(message);
        ekdsJSONResponse.setStatus("200");
        ekdsJSONResponse.setSuccess(false);
        return ekdsJSONResponse;
    }
    /**
     * TODO implements this method;
     */
    public static String httpGet(String url){


        return null;
    }
    /**
     * TODO implements this method;
     */
    public static String httpPOST(String url){


        return null;
    }
    /**
     * TODO implements this method;
     */

    public static String http(HttpMethod method,String url){
        HttpMethod flatHttpMethod=null;
        switch(method){
            case POST:{
                CloseableHttpClient HttpClient=null;
                break;
            }


            case GET:{

            }
            default: {
                flatHttpMethod = HttpMethod.GET;
            }
        }
        return null;
    }
}
