package net.unit8.sillage.example.employee.service;

import net.unit8.sillage.example.TestConfig;
import net.unit8.sillage.example.domain.EmailAddress;
import net.unit8.sillage.example.domain.Employee;
import net.unit8.sillage.example.domain.FirstName;
import net.unit8.sillage.example.domain.LastName;
import net.unit8.sillage.example.port.out.PromotePort;
import net.unit8.sillage.example.port.out.UpdateEmployeeStatePort;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfig.class)
class EmployeePromoteServiceTest {
    @Autowired
    private EmployeePromoteService employeePromoteService;

    @Autowired
    private PromotePort promotePort;

    @Autowired
    private UpdateEmployeeStatePort updateEmployeeStatePort;

    @Test
    void promote() {
        clearInvocations(updateEmployeeStatePort, promotePort);
        assertThat(employeePromoteService).isNotNull();
        int currentSalary = 100;
        int increasingSalary = 100;
        Employee employee = new Employee(
                new FirstName("Yoshitaka"),
                new LastName("Kawashima"),
                new EmailAddress("kawasima1016@gmail.com"),
                Money.of(currentSalary, "USD")
        );
        Money promotedSalary = Money.of(increasingSalary, "USD");
        Employee promotedEmployee = employeePromoteService.promote(employee, promotedSalary);

        assertThat(promotedEmployee.getSalary().getNumber().intValue())
                .isEqualTo(currentSalary + increasingSalary);

        verify(updateEmployeeStatePort, times(1))
                .updateEmployee(any());
        verify(updateEmployeeStatePort, times(0))
                .deleteEmployee(any());
        verify(promotePort, times(1))
                .promote(any(), any());
    }

    @Test
    void minusPromote() {
        clearInvocations(updateEmployeeStatePort, promotePort);
        assertThat(employeePromoteService).isNotNull();
        int currentSalary = 100;
        int increasingSalary = -100;
        Employee employee = new Employee(
                new FirstName("Yoshitaka"),
                new LastName("Kawashima"),
                new EmailAddress("kawasima1016@gmail.com"),
                Money.of(currentSalary, "USD")
        );
        Money promotedSalary = Money.of(increasingSalary, "USD");
        Employee promotedEmployee = employeePromoteService.promote(employee, promotedSalary);

        assertThat(promotedEmployee.getSalary().getNumber().intValue())
                .isEqualTo(currentSalary + increasingSalary);

        verify(updateEmployeeStatePort, times(1))
                .updateEmployee(any());
        verify(updateEmployeeStatePort, times(0))
                .deleteEmployee(any());
        verify(promotePort, times(1))
                .promote(any(), any());
    }

}