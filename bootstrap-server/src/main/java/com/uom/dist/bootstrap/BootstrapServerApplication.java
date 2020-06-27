package com.uom.dist.bootstrap;

import com.uom.dist.protocol.SharedConfigurationReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@SpringBootApplication
@Import(SharedConfigurationReference.class)
public class BootstrapServerApplication {
    private static final Logger logger = LoggerFactory.getLogger(BootstrapServerApplication.class);

    @Value("${udp.receiver.port}")
    private int udpPort;

    @Bean
    public SystemStartShutdownHandler startShutdownHandler() {
        return new SystemStartShutdownHandler();
    }

    public static void main(String[] args) {
        logger.info("Starting bootstrap server");
        SpringApplication.run(BootstrapServerApplication.class, args);
        logger.info("Started");
    }

    @Bean
    public IntegrationFlow processUniCastUdpMessage() {
        return IntegrationFlows
                .from(new UnicastReceivingChannelAdapter(udpPort))
                .handle("UDPServer", "handleMessage")
                .get();
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