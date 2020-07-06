package net.unit8.sillage.example.user.service;

import net.unit8.sillage.example.domain.EmailAddress;
import net.unit8.sillage.example.domain.Employee;
import net.unit8.sillage.example.domain.EmployeeId;
import net.unit8.sillage.example.port.out.LoadEmployeePort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmployeeSearchService {
    private final LoadEmployeePort loadEmployeePort;

    public EmployeeSearchService(LoadEmployeePort loadEmployeePort) {
        this.loadEmployeePort = loadEmployeePort;
    }

    public Employee findById(EmployeeId employeeId) {
        return loadEmployeePort.loadEmployee(employeeId);
    }

    public List<Employee> findAll() {
        return loadEmployeePort.loadAllEmployees();
    }

    public long countByEmail(EmailAddress emailAddress) {
        return loadEmployeePort.countByEmail(emailAddress);
    }
}
