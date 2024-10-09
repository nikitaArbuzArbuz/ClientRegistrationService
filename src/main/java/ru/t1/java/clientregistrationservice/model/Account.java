package ru.t1.java.clientregistrationservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts", schema = "bank")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accounts_seq")
    @SequenceGenerator(name = "accounts_seq", sequenceName = "accounts_seq", schema = "bank")
    @Column(name = "id")
    private Long id;

    @Column(name = "account_number")
    private String accountNumber;

    @ManyToOne
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "client_id")
    private Client client;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "balance", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal balance = new BigDecimal("0.00");

    @Column(name = "is_blocked")
    @Builder.Default
    private boolean isBlocked = false;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Transaction> transactions;

    public void blockAccount() {
        this.isBlocked = true;
    }

    public void unblockAccount() {
        this.isBlocked = false;
    }

    public void checkAndBlockCreditAccount(BigDecimal creditLimit) {
        if (accountType == AccountType.CREDIT && balance.compareTo(creditLimit.negate()) <= 0) {
            blockAccount();
        }
    }

    public enum AccountType {
        DEPOSIT, CREDIT
    }
}
