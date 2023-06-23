package com.project.winter.beans;

import com.project.winter.annotation.Component;
import com.project.winter.annotation.Configuration;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class BeanFactory {
    private static String packagePrefix = "com.project.winter";
    private static Reflections reflections;
    private static final Map<Class<?>, Object> beans = new HashMap<>();
    private static final List<Object> configurations = new ArrayList<>();

    private BeanFactory() {}

    public static void setTargetPackage(String packagePrefix) {
        BeanFactory.packagePrefix = packagePrefix;
    }

    public static void initialize() {
        reflections = new Reflections(packagePrefix);
        Set<Class<?>> preInstantiatedClazz = getClassTypeAnnotatedWith(Component.class);

        createBeansByConfiguration();
        createBeansByClass(preInstantiatedClazz);
    }

    private static Set<Class<?>> getClassTypeAnnotatedWith(Class<? extends Annotation> annotation) {
        Set<Class<?>> types = new HashSet<>();

        reflections.getTypesAnnotatedWith(annotation).forEach(type -> {
            if (!type.isAnnotation() && !type.isInterface()) {
                if (type.isAnnotationPresent(Configuration.class)) configurations.add(createInstance(type));
                else types.add(type);
            }
        });

        return types;
    }

    private static void createBeansByConfiguration() {
        configurations.forEach(BeanFactory::createBeanInConfigurationAnnotatedClass);
    }

    private static void createBeanInConfigurationAnnotatedClass(Object configuration) {
        Class<?> subclass = configuration.getClass();
        Map<Class<?>, Method> beanMethodNames = BeanFactoryUtils.getBeanAnnotatedMethodInConfiguration(subclass);

        List<Object> parameters = new ArrayList<>();

        beanMethodNames.forEach((clazz, method) -> {
            Arrays.stream(method.getParameterTypes()).forEach(parameterType -> {
                if (isBeanInitialized(clazz)) parameters.add(getBean(clazz));
                else parameters.add(createInstance(parameterType));
            });

            try {
                putBean(clazz, method.invoke(configuration, parameters.toArray()));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void createBeansByClass(Set<Class<?>> preInstantiatedClazz) {
        for (Class<?> clazz : preInstantiatedClazz) {
            if (isBeanInitialized(clazz)) continue;

            Object instance = createInstance(clazz);
            putBean(clazz, instance);
        }
    }

    private static Object createInstance(Class<?> clazz) {
        Constructor<?> constructor = findConstructor(clazz);

        try {
            return constructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Constructor<?> findConstructor(Class<?> clazz) {

        Set<Constructor> allConstructors = ReflectionUtils.getAllConstructors(clazz);

        Constructor<?> foundConstructor = null;
        for (Constructor constructor : allConstructors) {
            int parameterCount = constructor.getParameterCount();
            if (parameterCount == 0) {
                foundConstructor = constructor;
                break;
            }
        }

        return foundConstructor;
    }

    private static boolean isBeanInitialized(Class<?> clazz) {
        return beans.containsKey(clazz);
    }

    private static <T> T getBean(Class<T> clazz) {
        return (T) beans.get(clazz);
    }

    private static <T> void putBean(Class<T> clazz, Object bean) {
        beans.put(clazz, bean);
    }

}
