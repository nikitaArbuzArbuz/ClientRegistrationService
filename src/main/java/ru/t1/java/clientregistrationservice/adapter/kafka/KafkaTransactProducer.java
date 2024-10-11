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

    public void sendTransact(Long transactionId) {
        try {
            template.send("sendTransact-out-0", transactionId);
            log.info("Транзакции отправлены в топик: {}", transactionId);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
