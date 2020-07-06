package net.unit8.sillage.example.port.out;

import net.unit8.sillage.example.domain.EmailAddress;
import net.unit8.sillage.example.domain.Employee;
import net.unit8.sillage.example.domain.EmployeeId;

import java.util.List;

public interface LoadEmployeePort {
    Employee loadEmployee(EmployeeId employeeId);
    List<Employee> loadAllEmployees();
    long countByEmail(EmailAddress emailAddress);
}
