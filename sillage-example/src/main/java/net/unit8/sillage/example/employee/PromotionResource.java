package net.unit8.sillage.example.employee;

import net.unit8.sillage.Decision;
import net.unit8.sillage.data.RestContext;
import net.unit8.sillage.example.domain.Employee;
import net.unit8.sillage.example.domain.EmployeeId;
import net.unit8.sillage.example.employee.boundary.PromotionCreateRequest;
import net.unit8.sillage.example.employee.service.EmployeePromoteService;
import net.unit8.sillage.example.employee.service.EmployeeSearchService;
import net.unit8.sillage.resource.AllowedMethods;
import net.unit8.sillage.resource.DecisionContext;
import org.javamoney.moneta.Money;
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

import java.util.stream.Collectors;

import static net.unit8.sillage.DecisionPoint.*;

@Component
@AllowedMethods(HttpMethod.POST)
public class PromotionResource {
    private final Validator validator;
    private final EmployeeSearchService employeeSearchService;
    private final EmployeePromoteService employeePromoteService;

    public PromotionResource(EmployeeSearchService employeeSearchService,
                             EmployeePromoteService  employeePromoteService,
                             Validator validator) {
        this.employeeSearchService = employeeSearchService;
        this.employeePromoteService = employeePromoteService;
        this.validator = validator;
    }

    @Decision(value = MALFORMED, method = HttpMethod.POST)
    public Problem validateCreateRequest(RestContext context,
                                         @RequestBody PromotionCreateRequest createRequest,
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

    @Decision(PROCESSABLE)
    public boolean exists(@PathVariable Long id, RestContext context) {
        Employee employee = employeeSearchService.findById(new EmployeeId(id));
        if (employee != null) {
            context.putValue(employee);
        }
        return employee != null;
    }

    @Decision(POST)
    public Employee promote(@DecisionContext Employee employee,
                            @DecisionContext PromotionCreateRequest createRequest) {
        Money promotedSalary = Money.of(createRequest.getAmount(), createRequest.getCurrencyUnit());
        return employeePromoteService.promote(employee, promotedSalary);
    }
}
