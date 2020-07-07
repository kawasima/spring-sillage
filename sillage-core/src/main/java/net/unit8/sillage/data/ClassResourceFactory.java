package net.unit8.sillage.data;

import net.unit8.sillage.resource.DefaultResource;
import net.unit8.sillage.resource.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ClassResourceFactory {
    private final DecisionHandlerAdapter handlerAdapter;
    private final ApplicationContext context;
    private final Resource parent;
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
