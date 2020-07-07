package net.unit8.sillage.example.user.service;

import net.unit8.sillage.example.domain.Employee;
import net.unit8.sillage.example.port.out.PromotePort;
import net.unit8.sillage.example.port.out.UpdateEmployeeStatePort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.MonetaryConversions;
import java.util.UUID;

@Component
public class EmployeePromoteService {
    private final UpdateEmployeeStatePort updateEmployeeStatePort;
    private final PromotePort promotePort;
    private final ExchangeRateProvider rateProvider;
    private final PlatformTransactionManager transactionManager;

    public EmployeePromoteService(UpdateEmployeeStatePort updateEmployeeStatePort,
                                  PromotePort promotePort,
                                  PlatformTransactionManager transactionManager) {
        this.updateEmployeeStatePort = updateEmployeeStatePort;
        this.promotePort = promotePort;
        this.transactionManager = transactionManager;
        rateProvider = MonetaryConversions.getExchangeRateProvider("IMF");
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

        new TransactionTemplate(transactionManager).execute(status -> {
            updateEmployeeStatePort.updateEmployee(promotedEmployee);
            promotePort.promote(promotedEmployee, amount);
            return null;
        });
        return promotedEmployee;
    }
}
