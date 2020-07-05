package net.unit8.sillage.decision;

import net.unit8.sillage.DecisionPoint;
import net.unit8.sillage.data.RestContext;
import net.unit8.sillage.data.SimpleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.util.Optional;
import java.util.function.Function;

public class Handler implements Node<Object> {
    private static final Logger LOG = LoggerFactory.getLogger(Handler.class);

    private final DecisionPoint point;
    private final int statusCode;
    private Object message;

    public Handler(DecisionPoint point, int statusCode, String message) {
        this.point = point;
        this.statusCode = statusCode;
        if (message != null) {
            if (statusCode >= 400) {
                this.message = Problem.builder()
                        .withStatus(Status.valueOf(statusCode))
                        .withDetail(message)
                        .build();
            } else {
                this.message = new SimpleMessage(message);
            }
        }

    }

    @Override
    public Object execute(RestContext context) {
        LOG.info("{}", point.name());
        Function<RestContext, ?> ftest = context.getResourceFunction(point);
        if (ftest != null) {
            Object fres = ftest.apply(context);
            if (fres instanceof ResponseEntity) {
                return fres;
            } else if (fres == null) {
                message = null;
            } else if (fres instanceof String) {
                message = new SimpleMessage((String) fres);
            } else {
                message = fres;
            }
        }

        ResponseEntity<Object> response =  new ResponseEntity<>(message, HttpStatus.valueOf(statusCode));
        Optional.ofNullable(context.getHeaders())
                .ifPresent(headers -> response.getHeaders().putAll(headers));

        return response;
    }

    @Override
    public String toString() {
        return "Handler{" +
                "point=" + point +
                ", statusCode=" + statusCode +
                ", message=" + message +
                '}';
    }

}
