package net.unit8.sillage.example.domain;

import javax.money.MonetaryAmount;
import java.io.Serializable;
import java.util.UUID;

public class Employee implements Serializable {
    private final EmployeeId id;
    private final FirstName firstName;
    private final LastName lastName;
    private final EmailAddress emailAddress;
    private final MonetaryAmount salary;
    private final UUID version;

    public Employee(EmployeeId id,
                    FirstName firstName,
                    LastName lastName,
                    EmailAddress emailAddress,
                    MonetaryAmount salary,
                    UUID version) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.salary = salary;
        this.version = version;
    }

    public Employee(FirstName firstName,
                    LastName lastName,
                    EmailAddress emailAddress,
                    MonetaryAmount salary) {
        id = null;
        version = UUID.randomUUID();
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

    public UUID getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", emailAddress=" + emailAddress +
                ", salary=" + salary +
                ", version=" + version +
                '}';
    }
}
