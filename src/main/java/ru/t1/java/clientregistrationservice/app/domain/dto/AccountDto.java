package ru.t1.java.clientregistrationservice.app.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;

@Getter
@Setter
public class AccountDto {
    @NotBlank
    private Account.AccountType accountType;
}
