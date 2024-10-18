package ru.t1.java.clientregistrationservice.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import ru.t1.java.clientregistrationservice.adapter.kafka.KafkaTransactProducer;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaTransactProducerTests {
    @Mock
    private StreamBridge template;

    @InjectMocks
    private KafkaTransactProducer kafkaTransactProducer;

    @Test
    void sendTransactionToTopic() {
        Long transactionId = 1L;
        kafkaTransactProducer.sendTransact(transactionId);
        verify(template, times(1)).send("sendTransact-out-0", transactionId);
    }

    @Test
    void sendTransactShouldHandleException() {
        Long transactionId = 1L;
        doThrow(new RuntimeException("Simulated exception")).when(template).send(anyString(), any());
        kafkaTransactProducer.sendTransact(transactionId);
    }
}
