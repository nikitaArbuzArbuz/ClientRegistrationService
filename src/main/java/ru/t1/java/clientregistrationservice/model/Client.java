package ru.t1.java.clientregistrationservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "clients", schema = "bank")
public class Client extends AbstractPersistable<Long> {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @NotBlank
    @Size(max = 20)
    @Column(name = "login", unique = true)
    private String login;

    @Column(name = "email", unique = true)
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    @Column(name = "password")
    private String password;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Account> accounts;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoleEnum name;

    public enum RoleEnum {
        ROLE_USER,
        ROLE_ADMIN
    }
}
