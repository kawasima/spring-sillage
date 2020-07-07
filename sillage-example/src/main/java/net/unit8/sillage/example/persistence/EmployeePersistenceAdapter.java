package net.unit8.sillage.example.persistence;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import net.unit8.sillage.example.domain.*;
import net.unit8.sillage.example.persistence.entity.EmployeeEntity;
import net.unit8.sillage.example.port.out.LoadEmployeePort;
import net.unit8.sillage.example.port.out.UpdateEmployeeStatePort;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class EmployeePersistenceAdapter implements LoadEmployeePort, UpdateEmployeeStatePort {
    private final EmployeeRepository employeeRepository;

    public EmployeePersistenceAdapter(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    private Employee fromEntity(EmployeeEntity entity) {
        return new Employee(
                new EmployeeId(entity.getId()),
                new FirstName(entity.getFirstName()),
                new LastName(entity.getLastName()),
                new EmailAddress(entity.getEmail()),
                Money.of(entity.getSalary(), entity.getCurrencyUnit()),
                UUID.fromString(entity.getVersion()));
    }

    private EmployeeEntity toEntity(Employee employee) {
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

    private void addTxId(EmployeeEntity entity) {
        entity.setVersion(UUID.randomUUID().toString());
    }

    @Override
    public Employee loadEmployee(EmployeeId employeeId) {
        return employeeRepository.findById(employeeId.getValue())
                .map(this::fromEntity)
                .orElse(null);
    }

    @Override
    public List<Employee> loadAllEmployees() {
        return StreamSupport.stream(employeeRepository.findAll().spliterator(), false)
                .map(this::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public long countByEmail(EmailAddress emailAddress) {
        return employeeRepository.countByEmail(emailAddress.getSimpleAddress());
    }

    @Override
    public void updateEmployee(Employee employee) {
        EmployeeEntity entity = toEntity(employee);
        addTxId(entity);
        employeeRepository.save(entity);
    }

    @Override
    public void deleteEmployee(Employee employee) {
        employeeRepository.delete(toEntity(employee));
    }
}
