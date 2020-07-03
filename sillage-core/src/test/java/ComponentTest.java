import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver;

import java.lang.reflect.Method;
import java.util.Locale;

public class ComponentTest {
    static class HogeResource {
        public String abc(Locale locale) {
            return "hello";
        }
    }
    @Test
    void test() throws Exception {
        ServletRequestMethodArgumentResolver resolver = new ServletRequestMethodArgumentResolver();
        Method method = HogeResource.class.getMethod("abc", Locale.class);
        MethodParameter parameter = new MethodParameter(method, 0);
		ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        MockHttpServletRequest servletRequest = new MockHttpServletRequest("GET", "");
        ServletWebRequest webRequest = new ServletWebRequest(servletRequest, new MockHttpServletResponse());
        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, null);
        System.out.println(result);
    }
}
