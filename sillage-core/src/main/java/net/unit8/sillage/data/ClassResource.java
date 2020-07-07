package net.unit8.sillage.data;

import net.unit8.sillage.Decision;
import net.unit8.sillage.DecisionPoint;
import net.unit8.sillage.resource.AllowedMethods;
import net.unit8.sillage.resource.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClassResource implements Resource {
    private final Map<DecisionPoint, Function<RestContext, ?>> functions;
    private final Resource parent;
    private final Function<RestContext, ?> methodAllowedFunc;

    public ClassResource(Object instance,
                         Resource parent,
                         DecisionHandlerAdapter handlerAdapter) {
        this.parent = parent;
        Class<?> resourceClass = instance.getClass();
        Set<HttpMethod> allowedMethods = parseAllowedMethods(resourceClass);
        methodAllowedFunc = context -> allowedMethods.contains(context.getMethod());
        functions = new HashMap<>();
        Map<DecisionPoint, List<Method>> resourceMethods = Arrays.stream(resourceClass.getMethods())
            .filter(method -> method.getAnnotation(Decision.class) != null)
            .collect(Collectors.groupingBy(method -> {
                Decision decision = method.getAnnotation(Decision.class);
                return decision.value();
            }, Collectors.toList()));

        resourceMethods.forEach((point, methods) -> {
            Map<HttpMethod, HandlerMethod> httpMethodMap = new HashMap<>();
            final AtomicReference<HandlerMethod> fallbackMethod = new AtomicReference<>();
            methods.forEach(method -> {
                Decision decision = method.getAnnotation(Decision.class);
                HttpMethod[] targetMethods = decision.method();
                if (targetMethods.length > 0) {
                    for(HttpMethod m : targetMethods) {
                        httpMethodMap.put(m, new HandlerMethod(instance, method));
                    }
                } else {
                    fallbackMethod.set(new HandlerMethod(instance, method));
                }
            });

            functions.put(point, context -> {
                HandlerMethod handlerMethod = httpMethodMap.getOrDefault(
                    context.getMethod(),
                    fallbackMethod.get());
                if (handlerMethod != null) {
                    try {
                        handlerAdapter.handle(context.getRequest(), context.getResponse(), handlerMethod);
                        return context.getRequest().getAttribute("message");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    return parent.getFunction(point).apply(context);
                }
            });
        });
    }

    @Override
    public Function<RestContext, ?> getFunction(DecisionPoint point) {
        Function<RestContext, ?> f = functions.get(point);
        if (f != null) return f;
        if (point == DecisionPoint.METHOD_ALLOWED) {
            return methodAllowedFunc;
        }
        return parent.getFunction(point);
    }

   private Set<HttpMethod> parseAllowedMethods(Class<?> resourceClass) {
        AllowedMethods allowedMethods = resourceClass.getAnnotation(AllowedMethods.class);
        if (allowedMethods == null) {
            return new HashSet<>(Arrays.asList(HttpMethod.GET, HttpMethod.HEAD));
        } else {
            return Arrays.stream(allowedMethods.value())
                    .collect(Collectors.toSet());
        }
    }

}
