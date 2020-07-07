package net.unit8.sillage.example.persistence;

import net.unit8.sillage.example.domain.*;
import net.unit8.sillage.example.persistence.entity.EmployeeEntity;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
class EmployeeMapper {
    Employee fromEntity(EmployeeEntity entity) {
        return new Employee(
                new EmployeeId(entity.getId()),
                new FirstName(entity.getFirstName()),
                new LastName(entity.getLastName()),
                new EmailAddress(entity.getEmail()),
                Money.of(entity.getSalary(), entity.getCurrencyUnit()),
                UUID.fromString(entity.getVersion()));
    }

    EmployeeEntity toEntity(Employee employee) {
        EmployeeEntity entity = new EmployeeEntity();
        Optional.ofNullable(employee.getId())
                .ifPresent(id -> entity.setId(id.getValue()));
        entity.setFirstName(employee.getFirstName().getValue());
        entity.setLastName(employee.getLastName().getValue());
        entity.setEmail(employee.getEmailAddress().getSimpleAddress());
        entity.setCurrencyUnit(employee.getSalary().getCurrency().getCurrencyCode());
        entity.setSalary(employee.getSalary().getNumber().numberValue(BigDecimal.class));
        return entity;
    }
}
