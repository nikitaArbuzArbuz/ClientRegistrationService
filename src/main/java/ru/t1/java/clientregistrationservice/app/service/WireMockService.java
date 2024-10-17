package ru.t1.java.clientregistrationservice.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto;

@Service
@RequiredArgsConstructor
public class WireMockService {
    private final WebClient webClient;

    public boolean transactionPlug(TransactionDto transactionDto) {

        return Boolean.TRUE.equals(webClient.post()
                .uri("/approveTransaction")
                .bodyValue(transactionDto)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block());
    }
}
