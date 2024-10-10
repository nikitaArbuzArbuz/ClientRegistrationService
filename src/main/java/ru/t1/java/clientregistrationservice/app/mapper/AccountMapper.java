package ru.t1.java.clientregistrationservice.app.mapper;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;
import ru.t1.java.clientregistrationservice.app.domain.entity.Client;
import ru.t1.java.clientregistrationservice.app.domain.dto.AccountDto;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
@Slf4j
public abstract class AccountMapper {
    @Mapping(target = "accountNumber", expression = "java(generateAccountNumber())")
    @Mapping(target = "client", source = "client")
    @Mapping(target = "id", ignore = true)
    public abstract Account map(AccountDto accountDto, Client client);

    public String generateAccountNumber() {
        return UUID.randomUUID().toString();
    }
}