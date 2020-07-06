package net.unit8.sillage.example.user.service;

import net.unit8.sillage.example.domain.Employee;
import net.unit8.sillage.example.port.out.UpdateEmployeeStatePort;
import org.springframework.stereotype.Component;

@Component
public class EmployeeModifyService {
    private final UpdateEmployeeStatePort updateEmployeeStatePort;

    public EmployeeModifyService(UpdateEmployeeStatePort updateEmployeeStatePort) {
        this.updateEmployeeStatePort = updateEmployeeStatePort;
    }

    public void delete(Employee employee) {
        updateEmployeeStatePort.deleteEmployee(employee);
    }

    public void save(Employee employee) {
        updateEmployeeStatePort.updateEmployee(employee);
    }
}
