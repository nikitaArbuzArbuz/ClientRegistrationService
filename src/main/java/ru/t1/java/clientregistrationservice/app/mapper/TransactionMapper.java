package ru.t1.java.clientregistrationservice.app.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.app.domain.entity.Transaction;

import java.util.List;

@Mapper(componentModel = "spring")
@Slf4j
@RequiredArgsConstructor
public abstract class TransactionMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "transactionDate", expression = "java(java.time.LocalDateTime.now())")
    public abstract Transaction map(TransactionDto transactionDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "type", source = "type")
    public abstract TransactionDto map(Transaction transaction);

    @IterableMapping(elementTargetType = Transaction.class)
    public abstract List<Transaction> map(List<TransactionDto> transactionDtoList);
}
