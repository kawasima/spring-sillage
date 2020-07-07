package net.unit8.sillage.example.employee;

import net.unit8.sillage.Decision;
import net.unit8.sillage.data.RestContext;
import net.unit8.sillage.example.domain.*;
import net.unit8.sillage.example.employee.boundary.EmployeeUpdateRequest;
import net.unit8.sillage.example.employee.service.EmployeeModifyService;
import net.unit8.sillage.example.employee.service.EmployeeSearchService;
import net.unit8.sillage.resource.AllowedMethods;
import net.unit8.sillage.resource.DecisionContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.unit8.sillage.DecisionPoint.*;

@Component
@AllowedMethods({HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE})
public class EmployeeResource {
    private final Validator validator;
    private final EmployeeSearchService employeeSearchService;
    private final EmployeeModifyService employeeModifyService;

    public EmployeeResource(EmployeeSearchService employeeSearchService,
                            EmployeeModifyService  employeeModifyService,
                            Validator validator) {
        this.employeeSearchService = employeeSearchService;
        this.employeeModifyService = employeeModifyService;
        this.validator = validator;
    }

    @Decision(value = MALFORMED, method = HttpMethod.PUT)
    public Problem validateUpdateRequest(RestContext context,
                                         @RequestBody EmployeeUpdateRequest updateRequest,
                                         Errors errors) {
        ValidationUtils.invokeValidator(validator, updateRequest, errors);
        if (errors.hasErrors()) {
            return new ConstraintViolationProblem(Status.BAD_REQUEST,
                    errors.getFieldErrors().stream()
                            .map(e -> new Violation(e.getField(), e.getDefaultMessage()))
                            .collect(Collectors.toList()));
        } else {
            context.putValue(updateRequest);
            return null;
        }
    }

    @Decision(EXISTS)
    public boolean exists(@PathVariable Long id, RestContext context) {
        Employee employee = employeeSearchService.findById(new EmployeeId(id));
        if (employee != null) {
            context.putValue(employee);
        }
        return employee != null;
    }

    @Decision(ETAG)
    public String generateETag(@DecisionContext Employee employee) {
        return Optional.ofNullable(employee)
                .map(emp -> emp.getVersion().toString())
                .orElse(null);
    }

    @Decision(HANDLE_OK)
    public Employee user(@DecisionContext Employee employee) {
        return employee;
    }

    @Decision(DELETE)
    public void delete(@DecisionContext Employee employee) {
        employeeModifyService.save(employee);
    }

    @Decision(PUT)
    public void update(@DecisionContext Employee employee,
                       @DecisionContext EmployeeUpdateRequest updateRequest) {
        Employee employeeModified = new Employee(
                employee.getId(),
                new FirstName(updateRequest.getFirstName()),
                new LastName(updateRequest.getLastName()),
                new EmailAddress(updateRequest.getEmail()),
                employee.getSalary(),
                UUID.randomUUID());
        employeeModifyService.save(employeeModified);
    }

    @Decision(NEW)
    public boolean isNew(@DecisionContext Employee employee) {
        return employee == null;
    }
}
