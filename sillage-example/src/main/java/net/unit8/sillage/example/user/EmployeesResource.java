package net.unit8.sillage.example.user;

import net.unit8.sillage.Decision;
import net.unit8.sillage.data.RestContext;
import net.unit8.sillage.example.domain.EmailAddress;
import net.unit8.sillage.example.domain.Employee;
import net.unit8.sillage.example.domain.FirstName;
import net.unit8.sillage.example.domain.LastName;
import net.unit8.sillage.example.persistence.entity.EmployeeEntity;
import net.unit8.sillage.example.user.boundary.UserCreateRequest;
import net.unit8.sillage.example.user.service.EmployeeModifyService;
import net.unit8.sillage.example.user.service.EmployeeSearchService;
import net.unit8.sillage.resource.AllowedMethods;
import net.unit8.sillage.resource.DecisionContext;
import org.javamoney.moneta.Money;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestBody;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import javax.money.CurrencyUnit;
import java.util.List;
import java.util.stream.Collectors;

import static net.unit8.sillage.DecisionPoint.*;

@Component
@AllowedMethods({HttpMethod.POST, HttpMethod.GET})
public class EmployeesResource {
    private final Validator validator;
    private final EmployeeSearchService employeeSearchService;
    private final EmployeeModifyService employeeModifyService;

    public EmployeesResource(EmployeeSearchService employeeSearchService,
                             EmployeeModifyService employeeModifyService,
                             Validator validator) {
        this.employeeSearchService = employeeSearchService;
        this.employeeModifyService = employeeModifyService;
        this.validator = validator;
    }

    @Decision(value = MALFORMED, method = HttpMethod.POST)
    public Problem malformed(RestContext context,
                            @RequestBody UserCreateRequest createRequest,
                            Errors errors) {
        ValidationUtils.invokeValidator(validator, createRequest, errors);
        if (errors.hasErrors()) {
            return new ConstraintViolationProblem(Status.BAD_REQUEST,
                    errors.getFieldErrors().stream()
                            .map(e -> new Violation(e.getField(), e.getDefaultMessage()))
                            .collect(Collectors.toList()));
        } else {
            context.putValue(createRequest);
            return null;
        }
    }

    @Decision(value = CONFLICT, method = HttpMethod.POST)
    public Problem conflict(RestContext context,
                            @DecisionContext UserCreateRequest createRequest) {
        long cnt = employeeSearchService.countByEmail(new EmailAddress(createRequest.getEmail()));
        if (cnt > 0) {
            return new ConstraintViolationProblem(Status.CONFLICT, List.of(new Violation("email", "duplicated")));
        }
        return null;
    }

    @Decision(POST)
    public Employee saveUser(@DecisionContext UserCreateRequest createRequest) {
        Employee employee = new Employee(
                new FirstName(createRequest.getFirstName()),
                new LastName(createRequest.getLastName()),
                new EmailAddress(createRequest.getEmail()),
                Money.of(10000, "USD")
        );
        BeanUtils.copyProperties(createRequest, employee);
        employeeModifyService.save(employee);
        return employee;
    }

    @Decision(HANDLE_OK)
    public Iterable<Employee> listUser() {
        return employeeSearchService.findAll();
    }
}
