package com.primeton.cmcc.cmccflowguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CmccFlowGuardApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmccFlowGuardApplication.class, args);
    }

}
