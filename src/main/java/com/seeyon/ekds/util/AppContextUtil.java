package com.seeyon.ekds.util;

import com.seeyon.EkdsApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * Created by liuwenping on 2019/9/2.
 * @Author lwp
 */
public final class AppContextUtil {

    public static ApplicationContext getApplicationContext() {
        return EkdsApplication.getApplicationContext();
    }

    public static <T>  T getBean(String s) {
        return (T)getApplicationContext().getBean(s);
    }

    public static <T> T getBean(Class<T> cls) {

        return getApplicationContext().getBean(cls);
    }

    public String getApplicationName() {
        return getApplicationContext().getApplicationName();
    }

    public Environment getEnvironment() {
        return getApplicationContext().getEnvironment();
    }


}
