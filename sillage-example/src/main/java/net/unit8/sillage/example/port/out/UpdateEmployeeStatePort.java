package net.unit8.sillage.example.port.out;

import net.unit8.sillage.example.domain.Employee;

public interface UpdateEmployeeStatePort {
    void updateEmployee(Employee employee);
    void deleteEmployee(Employee employee);
}
