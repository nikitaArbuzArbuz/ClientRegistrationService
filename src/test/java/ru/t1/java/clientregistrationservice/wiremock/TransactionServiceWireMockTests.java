package ru.t1.java.clientregistrationservice.wiremock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.t1.java.clientregistrationservice.adapter.repository.AccountRepository;
import ru.t1.java.clientregistrationservice.adapter.repository.TransactionRepository;
import ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;
import ru.t1.java.clientregistrationservice.app.domain.entity.Transaction;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.resetAllRequests;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureWebClient
public class TransactionServiceWireMockTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Account account;
    private TransactionDto transactionDto;

    @BeforeEach
    void setup() {
        resetAllRequests();
        account = new Account();
        account.setAccountType(Account.AccountType.DEPOSIT);
        account.setBlocked(false);
        account.setBalance(new BigDecimal("1000.00"));
        account.setAccountNumber("123456");

        transactionDto = new TransactionDto();
        transactionDto.setAmount(new BigDecimal("100.00"));
        transactionDto.setType(Transaction.TransactionType.ADD);
        transactionDto.setDescription("Test transaction");

        accountRepository.save(account);
    }

    @Test
    public void testTransactionApproval() throws Exception {

        String transactionJson = "{\"accountId\": " + account.getId() + ", " +
                "\"amount\": 100.00, \"description\": \"Test transaction\", \"type\": \"ADD\"}";

        mockMvc.perform(post("/api/transact/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson))
                .andExpect(status().isOk())
                .andExpect(header().string("Server", "Transaction success!"));

        Transaction transaction = transactionRepository.findById(1L).orElse(null);
        assertNotNull(transaction);
        assertEquals(transactionDto.getAmount(), transaction.getAmount());
        assertEquals(transactionDto.getDescription(), transaction.getDescription());
    }

    @Test
    public void testTransactionApprovalShouldFailWhenAccountIsBlocked() throws Exception {
        account.setBlocked(true);
        accountRepository.save(account);

        String transactionJson = "{\"accountId\": " + account.getId() + ", " +
                "\"amount\": 100.00, \"description\": \"Test transaction\", \"type\": \"ADD\"}";

        mockMvc.perform(post("/api/transact/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson))
                .andExpect(status().isOk());

        assertNull(transactionRepository.findById(account.getId()).orElse(null));
    }
}
