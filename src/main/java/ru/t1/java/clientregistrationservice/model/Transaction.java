package ru.t1.java.clientregistrationservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions", schema = "bank")
public class Transaction extends AbstractPersistable<Long> {

    @ManyToOne
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "description")
    private String description;
}
