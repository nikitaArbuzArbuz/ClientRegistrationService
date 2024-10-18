package ru.t1.java.clientregistrationservice.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.web.CheckResponse;
import ru.t1.java.clientregistrationservice.web.CheckWebClient;

@Service
@RequiredArgsConstructor
public class WireMockService {
    private final WebClient webClient;
    private final CheckWebClient checkWebClient;

    public boolean transactionPlug(TransactionDto transactionDto) {
        return checkWebClient.check(transactionDto.getAccountId())
                .map(CheckResponse::getBlocked)
                .orElse(false);
    }
}
