package ru.t1.java.clientregistrationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles", schema = "bank")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_seq")
    @SequenceGenerator(name = "roles_seq", sequenceName = "roles_seq", schema = "bank")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoleEnum name;
}