package net.unit8.sillage.example.user.service;

import net.unit8.sillage.example.domain.Employee;
import net.unit8.sillage.example.port.out.UpdateEmployeeStatePort;
import org.springframework.stereotype.Component;

import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.MonetaryConversions;
import java.util.UUID;

@Component
public class EmployeePromoteService {
    private final UpdateEmployeeStatePort updateEmployeeStatePort;
    private final ExchangeRateProvider rateProvider;

    public EmployeePromoteService(UpdateEmployeeStatePort updateEmployeeStatePort) {
        this.updateEmployeeStatePort = updateEmployeeStatePort;
        rateProvider = MonetaryConversions.getExchangeRateProvider("IDENT");
    }

    public Employee promote(Employee employee, MonetaryAmount amount) {
        CurrencyConversion currencyConversion = rateProvider.getCurrencyConversion(employee.getSalary().getCurrency());
        MonetaryAmount convertedAmount = currencyConversion.apply(amount);
        MonetaryAmount newSalary = employee.getSalary().add(convertedAmount);

        Employee promotedEmployee = new Employee(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmailAddress(),
                newSalary,
                UUID.randomUUID()
        );
        updateEmployeeStatePort.updateEmployee(promotedEmployee);
        return promotedEmployee;
    }
}
