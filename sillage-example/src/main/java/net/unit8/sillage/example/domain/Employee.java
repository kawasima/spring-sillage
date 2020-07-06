package net.unit8.sillage.example.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.money.MonetaryAmount;
import java.io.Serializable;

public class Employee implements Serializable {
    private final EmployeeId id;
    private final FirstName firstName;
    private final LastName lastName;
    private final EmailAddress emailAddress;
    private final MonetaryAmount salary;

    public Employee(EmployeeId id,
                    FirstName firstName,
                    LastName lastName,
                    EmailAddress emailAddress,
                    MonetaryAmount salary) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.salary = salary;
    }

    public Employee(FirstName firstName,
                    LastName lastName,
                    EmailAddress emailAddress,
                    MonetaryAmount salary) {
        id = null;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.salary = salary;
    }

    public EmployeeId getId() {
        return id;
    }

    public FirstName getFirstName() {
        return firstName;
    }

    public LastName getLastName() {
        return lastName;
    }

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    public MonetaryAmount getSalary() {
        return salary;
    }
}
