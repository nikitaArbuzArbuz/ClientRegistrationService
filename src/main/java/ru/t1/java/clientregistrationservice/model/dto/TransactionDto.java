package ru.t1.java.clientregistrationservice.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.t1.java.clientregistrationservice.model.Transaction;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class TransactionDto {
    private Long accountId;
    private BigDecimal amount;
    private String description;
    private Transaction.TransactionType type;
}
