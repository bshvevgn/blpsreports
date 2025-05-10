package com.eg.blpsreports.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class TopicConfig {
    private final KafkaProperty kafkaProperty;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configs.put("offsets.topic.replication.factor", "1");
        configs.put("transaction.state.log.replication.factor", "1");
        configs.put("transaction.state.log.min.isr", "1");
        return new KafkaAdmin(configs);
    }

    @Bean
    public List<NewTopic> createTopics() {
        List<NewTopic> topics = new ArrayList<>();
        KafkaProperty.TopicConfigProperty digestReportTopic = kafkaProperty.getTopics().getDigestReport();
        topics.add(new NewTopic(digestReportTopic.getName(), digestReportTopic.getPartitions(), digestReportTopic.getReplicationFactor()));

        KafkaProperty.TopicConfigProperty bookingReportTopic = kafkaProperty.getTopics().getBookingReport();
        topics.add(new NewTopic(bookingReportTopic.getName(), bookingReportTopic.getPartitions(), bookingReportTopic.getReplicationFactor()));

        KafkaProperty.TopicConfigProperty emailSenderTopic = kafkaProperty.getTopics().getEmailSender();
        topics.add(new NewTopic(emailSenderTopic.getName(), emailSenderTopic.getPartitions(), emailSenderTopic.getReplicationFactor()));
        return topics;
    }
}

