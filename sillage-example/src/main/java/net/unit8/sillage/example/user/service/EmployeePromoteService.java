package net.unit8.sillage.example.user.service;

import net.unit8.sillage.example.domain.Employee;
import net.unit8.sillage.example.port.out.UpdateEmployeeStatePort;
import org.springframework.stereotype.Component;

@Component
public class EmployeePromoteService {
    private final UpdateEmployeeStatePort updateEmployeeStatePort;

    public EmployeePromoteService(UpdateEmployeeStatePort updateEmployeeStatePort) {
        this.updateEmployeeStatePort = updateEmployeeStatePort;
    }

    public Employee promote(Employee employee) {
        updateEmployeeStatePort.updateEmployee(employee);
        return employee;
    }
}
