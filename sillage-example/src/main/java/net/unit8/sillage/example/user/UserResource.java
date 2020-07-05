package net.unit8.sillage.example.user;

import net.unit8.sillage.Decision;
import net.unit8.sillage.data.RestContext;
import net.unit8.sillage.example.entity.User;
import net.unit8.sillage.example.user.boundary.UserUpdateRequest;
import net.unit8.sillage.resource.AllowedMethods;
import net.unit8.sillage.resource.DecisionContext;
import org.springframework.beans.BeanUtils;
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
@AllowedMethods({HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE})
public class UserResource {
    private final Validator validator;
    private final UserRepository userRepository;

    public UserResource(UserRepository userRepository,
                        Validator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }

    @Decision(value = MALFORMED, method = HttpMethod.PUT)
    public Problem validateUpdateRequest(RestContext context,
                                         @RequestBody UserUpdateRequest updateRequest,
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
        userRepository.findById(id).ifPresent(context::putValue);
        return context.getValue(User.class).isPresent();
    }

    @Decision(HANDLE_OK)
    public User user(@DecisionContext User user) {
        return user;
    }

    @Decision(DELETE)
    public void delete(@DecisionContext User user) {
        userRepository.delete(user);
    }

    @Decision(PUT)
    public void update(@DecisionContext User user,
                       @DecisionContext UserUpdateRequest updateRequest) {
        BeanUtils.copyProperties(updateRequest, user);
        userRepository.save(user);
    }

    @Decision(NEW)
    public boolean isNew(@DecisionContext User user) {
        return user == null;
    }
}
