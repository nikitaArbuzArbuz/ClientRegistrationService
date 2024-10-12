package ru.t1.java.clientregistrationservice.app.mapper;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.t1.java.clientregistrationservice.app.domain.dto.ClientDto;
import ru.t1.java.clientregistrationservice.app.domain.entity.Client;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class ClientMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "login", source = "login")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "accounts", source = "accounts")
    @Mapping(target = "roles", source = "roles")
    public abstract ClientDto map(Client client);
}