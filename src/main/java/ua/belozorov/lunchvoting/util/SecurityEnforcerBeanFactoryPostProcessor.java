package ua.belozorov.lunchvoting.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;
import ua.belozorov.lunchvoting.exceptions.InSecuredControllerMethodException;
import ua.belozorov.lunchvoting.web.security.InSecure;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * This BeanFactoryPostProcessor checks bean definitions for classes annotated
 * with {@code RestConroller}. If such classes' public methods are not marked with {@link org.springframework.security.access.annotation.Secured}
 * annotation/meta-annotation, {@link InSecuredControllerMethodException} exception is thrown
 *
 * Created on 22.02.17.
 */
public class SecurityEnforcerBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
        List<Pair<Class<?>,Method>> unSecuredMethods = new ArrayList<>();
        String[] restDefinitions = factory.getBeanNamesForAnnotation(RestController.class);
        for ( String name : restDefinitions) {
            BeanDefinition beanDefinition = factory.getBeanDefinition(name);
            Class<?> clas;
            try {
                clas = Class.forName(beanDefinition.getBeanClassName());
                if (AnnotationUtils.findAnnotation(clas, Secured.class) != null) {
                    continue;
                }
                Method[] declaredMethods = clas.getDeclaredMethods();
                for (Method method : declaredMethods) {
                    int modifiers = method.getModifiers();
                    if (Modifier.isPublic(modifiers)
                            && ! Modifier.isStatic(modifiers)
                            && AnnotationUtils.findAnnotation(method, Secured.class) == null
                            && AnnotationUtils.findAnnotation(method, InSecure.class) == null) {
                        unSecuredMethods.add(Pair.pairOf(clas, method));
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if ( ! unSecuredMethods.isEmpty()) {
            throw new InSecuredControllerMethodException(unSecuredMethods);
        }
    }
}
