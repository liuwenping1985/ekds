package com.seeyon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/**
 * 启动类
 *
 * @author liuwenping
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.seeyon.ekds"})
@EnableJpaRepositories(basePackages = {"com.seeyon.ekds"})
@EntityScan(basePackages = "com.seeyon.ekds")
public class EkdsApplication {

    private static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        System.out.println(" _____   _  __  ____    ____     ____ _           _         ___                \n" +
                "| ____| | |/ / |  _ \\  / ___|   / ___(_)_ __   __| | __ _  |_ _|_ __   ___     \n" +
                "|  _|   | ' /  | | | | \\___ \\  | |   | | '_ \\ / _` |/ _` |  | || '_ \\ / __|    \n" +
                "| |___  | . \\  | |_| |  ___) | | |___| | | | | (_| | (_| |  | || | | | (__   _ \n" +
                "|_____| |_|\\_\\ |____/  |____/   \\____|_|_| |_|\\__,_|\\__,_| |___|_| |_|\\___| (_)");
        applicationContext = SpringApplication.run(EkdsApplication.class, args);
        System.out.println(" ____                  _                _             _           _   _ \n" +
                "/ ___|  ___ _ ____   _(_) ___ ___   ___| |_ __ _ _ __| |_ ___  __| | | |\n" +
                "\\___ \\ / _ \\ '__\\ \\ / / |/ __/ _ \\ / __| __/ _` | '__| __/ _ \\/ _` | | |\n" +
                " ___) |  __/ |   \\ V /| | (_|  __/ \\__ \\ || (_| | |  | ||  __/ (_| | |_|\n" +
                "|____/ \\___|_|    \\_/ |_|\\___\\___| |___/\\__\\__,_|_|   \\__\\___|\\__,_| (_)");

        System.out.print("Total cost time:"+(System.currentTimeMillis()-start)+"ms");

    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
