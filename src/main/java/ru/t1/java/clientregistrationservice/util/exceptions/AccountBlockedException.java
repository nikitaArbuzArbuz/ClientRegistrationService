package ru.t1.java.clientregistrationservice.util.exceptions;

public class AccountBlockedException extends RuntimeException {
    private Long transactionId;

    public AccountBlockedException(String message, Long transactionId) {
        super(message);
        this.transactionId = transactionId;
    }

    public AccountBlockedException(String message) {
        super(message);
    }

    public Long getAccountId() {
        return transactionId;
    }
}
