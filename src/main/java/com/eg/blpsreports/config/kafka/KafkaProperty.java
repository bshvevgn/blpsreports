package com.eg.blpsreports.config.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperty {
    private List<String> bootstrapServers;
    private TopicProperty topics;

    @Data
    public static class TopicProperty {
        private TopicConfigProperty digestReport;
        private TopicConfigProperty bookingReport;
        private TopicConfigProperty emailSender;
    }

    @Data
    public static class TopicConfigProperty {
        private String name;
        private int partitions;
        private short replicationFactor;
    }
}
