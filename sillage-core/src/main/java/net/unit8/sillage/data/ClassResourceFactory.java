package net.unit8.sillage.data;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.lang.reflect.InvocationTargetException;

@Component
public class ClassResourceFactory {
    private DecisionHandlerAdapter handlerAdapter;
    private ApplicationContext context;
    private Resource parent;
    public ClassResourceFactory(DecisionHandlerAdapter handlerAdapter,
                                ApplicationContext context) {
        this.handlerAdapter = handlerAdapter;
        this.context = context;
        parent = new DefaultResource();
    }

    public ClassResource create(Class<?> resourceClass) {
        try {
            Object resource = context.getBean(resourceClass);
            return new ClassResource(resource, parent, handlerAdapter);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
