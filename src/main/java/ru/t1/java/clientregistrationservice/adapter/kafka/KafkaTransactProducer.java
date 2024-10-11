package ru.t1.java.clientregistrationservice.adapter.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaTransactProducer {
    private final StreamBridge template;

    public void send(String topic, Object o) {
        try {
            template.send(topic, o);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
