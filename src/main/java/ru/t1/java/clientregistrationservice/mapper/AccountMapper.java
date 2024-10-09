package ru.t1.java.clientregistrationservice.mapper;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.t1.java.clientregistrationservice.model.Account;
import ru.t1.java.clientregistrationservice.model.Client;
import ru.t1.java.clientregistrationservice.model.dto.AccountDto;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
@Slf4j
public abstract class AccountMapper {
    @Mapping(target = "accountNumber", expression = "java(generateAccountNumber())")
    @Mapping(target = "client", source = "client")
    public abstract Account map(AccountDto accountDto, Client client);

    public String generateAccountNumber() {
        return UUID.randomUUID().toString();
    }
}