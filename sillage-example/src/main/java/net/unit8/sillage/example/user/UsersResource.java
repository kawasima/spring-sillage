package net.unit8.sillage.example.user;

import net.unit8.sillage.Decision;
import net.unit8.sillage.data.RestContext;
import net.unit8.sillage.example.entity.User;
import net.unit8.sillage.example.user.boundary.UserCreateRequest;
import net.unit8.sillage.resource.AllowedMethods;
import net.unit8.sillage.resource.DecisionContext;
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

import java.util.List;
import java.util.stream.Collectors;

import static net.unit8.sillage.DecisionPoint.*;

@Component
@AllowedMethods({HttpMethod.POST, HttpMethod.GET})
public class UsersResource {
    private final Validator validator;
    private final UserRepository userRepository;

    public UsersResource(UserRepository userRepository,
                         Validator validator) {
        this.userRepository = userRepository;
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
        long cnt = userRepository.countByEmail(createRequest.getEmail());
        if (cnt > 0) {
            return new ConstraintViolationProblem(Status.CONFLICT, List.of(new Violation("email", "duplicated")));
        }
        return null;
    }

    @Decision(POST)
    public User saveUser(@DecisionContext UserCreateRequest createRequest) {
        User user = new User();
        BeanUtils.copyProperties(createRequest, user);
        userRepository.save(user);
        return user;
    }

    @Decision(HANDLE_OK)
    public Iterable<User> listUser() {
        return userRepository.findAll();
    }
}
