package com.uom.dist.node;

import com.uom.dist.protocol.SharedConfigurationReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@SpringBootApplication
@Import(SharedConfigurationReference.class)
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
            logger.info("node server start up");
        }

        @PreDestroy
        public void shutdown() {
            logger.info("node server shut down");
        }
    }

}