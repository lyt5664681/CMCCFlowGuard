package com.primeton.cmcc.cmccflowguard;

import com.primeton.cmcc.cmccflowguard.handle.LogFileWatcher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@EnableScheduling
@SpringBootApplication
public class CmccFlowGuardApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmccFlowGuardApplication.class, args);
    }

    @Bean
    public LogFileWatcher logFileWatcher() {
        String logFilePath = "logs" + File.separator + "debug.log";
        Path path = Paths.get(logFilePath);
        LogFileWatcher logFileWatcher = new LogFileWatcher(path);
        try {
            logFileWatcher.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logFileWatcher;
    }

}
