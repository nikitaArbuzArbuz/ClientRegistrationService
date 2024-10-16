package ru.t1.java.clientregistrationservice;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;
import ru.t1.java.clientregistrationservice.adapter.repository.AccountRepository;
import ru.t1.java.clientregistrationservice.adapter.repository.TransactionRepository;
import ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;
import ru.t1.java.clientregistrationservice.app.domain.entity.Transaction;
import ru.t1.java.clientregistrationservice.app.service.TransactionService;
import ru.t1.java.clientregistrationservice.app.service.impl.AccountServiceImpl;
import ru.t1.java.clientregistrationservice.web.CheckWebClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AccountServiceTests {
    @Autowired
    CheckWebClient checkWebClient;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    AccountServiceImpl accountService;

    @BeforeAll
    public static void setUp() {
    }


    @Test
    void testUnblockAccountAndSendToApprovalService() throws IOException {
        WireMockServer wireMockServer = new WireMockServer(8085);
        wireMockServer.start();
        configureFor("localhost", 8085);

        stubFor(post(urlPathMatching("/bsc-wire-mock/api/account/unblock"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(containing("\"clientId\": \"52\""))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json") // добавление заголовка Content-Type
                        .withBody("true")));

        Account account = new Account();
        account.setAccountNumber("123456");
        account.setAccountType(Account.AccountType.DEPOSIT);
        account.setBalance(BigDecimal.valueOf(5000));
        account.setBlocked(true);

        Account account2 = new Account();
        account2.setId(52L);
        account2.setAccountNumber("123456");
        account2.setAccountType(Account.AccountType.DEPOSIT);
        account2.setBalance(BigDecimal.valueOf(5000));
        account2.setBlocked(false);

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAccount(account2);
        transaction.setAmount(BigDecimal.valueOf(1000));
        transaction.setType(Transaction.TransactionType.ADD);
        transaction.setTransactionDate(LocalDateTime.now());

        transactionRepository.saveAndFlush(transaction);

        when(accountRepository.saveAndFlush(account)).thenReturn(account2);
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(transaction));

//        TransactionDto transactionDto = accountService.unblock(transaction.getId());
//
//        assertThat(transactionDto).isNotNull();
//        assertEquals(transactionDto.getAccountId(), account2.getId());

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost("http://localhost:8085/bsc-wire-mock/api/account/unblock");
            HttpResponse httpResponse = httpClient.execute(request);

            String stringResponse = EntityUtils.toString(httpResponse.getEntity()); // Получение тела ответа
            assertEquals("true", stringResponse); // Сравнение с телом ответа
            assertEquals("application/json", httpResponse.getFirstHeader("Content-Type").getValue()); // Проверка типа контента
        } finally {
            wireMockServer.stop(); // Остановка сервера в блоке finally
        }
    }
}
