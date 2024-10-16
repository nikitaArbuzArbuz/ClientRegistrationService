package ru.t1.java.clientregistrationservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureWebClient
public class TransactionServiceWireMockTests {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        resetAllRequests();
    }

    @Test
    public void testTransactionApproval() throws Exception {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.post(urlEqualTo("/approveTransaction"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")
                        .withStatus(200)));

        String transactionJson = "{\"accountId\": 1, \"amount\": 100.00, \"description\": \"Test transaction\", \"type\": \"ADD\"}";

        mockMvc.perform(post("/api/transact/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson))
                .andExpect(status().isOk())
                .andExpect(header().string("Server", "Transaction success!"));
    }
}
