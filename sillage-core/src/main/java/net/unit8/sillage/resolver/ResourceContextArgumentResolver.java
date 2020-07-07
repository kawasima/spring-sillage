package net.unit8.sillage.resolver;

import net.unit8.sillage.data.RestContext;
import net.unit8.sillage.resource.DecisionContext;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

public class ResourceContextArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(RestContext.class)
                || parameter.hasParameterAnnotation(DecisionContext.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        RestContext context = (RestContext) request.getAttribute(RestContext.class.getName());

        if (parameter.getParameterType().equals(RestContext.class)) {
            return context;
        } else {
            return context.getValue(parameter.getParameterType()).orElse(null);
        }
    }
}
