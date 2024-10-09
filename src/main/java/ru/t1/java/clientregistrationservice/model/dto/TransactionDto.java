package ru.t1.java.clientregistrationservice.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionDto {
    private Long accountId;
    private BigDecimal amount;
    private String description;
}
