package ru.t1.java.clientregistrationservice.adapter.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.app.service.TransactionService;

import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaTransactionConsumer {

    private final TransactionService transactionService;

//    @KafkaListener(id = "${t1.kafka.consumer.group-id}",
//            topics = "${t1.kafka.topic.client_transactions}",
//            containerFactory = "kafkaListenerContainerFactory")

    @Bean
    Consumer<List<TransactionDto>> listen() {
        return this::listener;
    }

    private void listener(List<TransactionDto> messageList) {
        transactionService.recordTransaction(messageList);
    }
}
