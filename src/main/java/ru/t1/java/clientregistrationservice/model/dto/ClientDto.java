package ru.t1.java.clientregistrationservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.clientregistrationservice.model.Account;
import ru.t1.java.clientregistrationservice.model.Role;

import java.util.Set;

/**
 * DTO for {@link ru.t1.java.clientregistrationservice.model.Client}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientDto {
    String firstName;
    String lastName;
    @Size(max = 20)
    @NotBlank
    String login;
    @Size(max = 50)
    @Email
    @NotBlank
    String email;
    Set<Account> accounts;
    Set<Role> roles;
}