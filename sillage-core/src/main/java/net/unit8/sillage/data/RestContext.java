package net.unit8.sillage.data;

import net.unit8.sillage.DecisionPoint;
import net.unit8.sillage.resource.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class RestContext {
    private final Resource resource;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final Map<Object, Object> values;
    private Object message;
    private int status;
    private HttpHeaders headers;

    public RestContext(Resource resource, HttpServletRequest request, HttpServletResponse response) {
        this.resource = resource;
        this.request = request;
        this.response = response;
        this.values = new HashMap<>();
        request.setAttribute(RestContext.class.getName(), this);
    }

    public Function<RestContext,?> getResourceFunction(DecisionPoint point) {
        return resource.getFunction(point);
    }

    public HttpServletRequest getRequest() {
        return request;
    }
    public HttpServletResponse getResponse() {
        return response;
    }

    public Optional<Integer> getStatus() {
        return status == 0 ? Optional.empty() : Optional.of(status);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public HttpMethod getMethod() {
        return HttpMethod.valueOf(request.getMethod());
    }

    public Optional<Object> getMessage() {
        return message == null ? Optional.empty() : Optional.of(message);
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public <V> void putValue(V value) {
        if (value == null) return;
        values.put(value.getClass(), value);
    }

    @SuppressWarnings("unchecked")
    public <K> Optional<K> getValue(Class<K> key) {
        K value = (K) values.get(key);
        return value == null ? Optional.empty() : Optional.of(value);
    }
}
