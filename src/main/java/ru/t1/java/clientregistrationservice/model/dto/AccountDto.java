package ru.t1.java.clientregistrationservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ru.t1.java.clientregistrationservice.model.Account;

@Getter
@Setter
public class AccountDto {
    @NotBlank
    private Account.AccountType accountType;
}
