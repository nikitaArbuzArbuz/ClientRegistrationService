package ru.t1.java.clientregistrationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.clientregistrationservice.mapper.TransactionMapper;
import ru.t1.java.clientregistrationservice.model.Transaction;
import ru.t1.java.clientregistrationservice.model.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.repository.AccountRepository;
import ru.t1.java.clientregistrationservice.service.TransactionService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaTransactionConsumer {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final AccountRepository accountRepository;

    @KafkaListener(id = "${t1.kafka.consumer.group-id}",
            topics = "${t1.kafka.topic.client_transactions}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Payload List<TransactionDto> messageList,
                       Acknowledgment ack,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        try {
            transactionService.recordTransaction(messageList);
        } finally {
            ack.acknowledge();
        }
    }
}
