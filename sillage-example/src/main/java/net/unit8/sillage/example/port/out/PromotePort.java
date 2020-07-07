package net.unit8.sillage.example.port.out;

import net.unit8.sillage.example.domain.Employee;

import javax.money.MonetaryAmount;

public interface PromotePort {
    void promote(Employee employee, MonetaryAmount promotedSalary);
}
