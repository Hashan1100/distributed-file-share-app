package com.uom.dist.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@SpringBootApplication
public class NetworkNodeApplication {
    private static final Logger logger = LoggerFactory.getLogger(NetworkNodeApplication.class);

    @Bean
    public SystemStartShutdownHandler startShutdownHandler() {
        return new SystemStartShutdownHandler();
    }

    public static void main(String[] args) {
        logger.info("Starting network node");
        SpringApplication.run(NetworkNodeApplication.class, args);
        logger.info("Started");
    }

    private static class SystemStartShutdownHandler {
        @PostConstruct
        public void startup() {
            logger.info("bootstrap server start up");
        }

        @PreDestroy
        public void shutdown() {
            logger.info("bootstrap server shut down");
        }
    }

}