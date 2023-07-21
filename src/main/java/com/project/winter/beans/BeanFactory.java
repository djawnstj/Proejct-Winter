package com.project.winter.beans;

import com.project.winter.annotation.Bean;
import com.project.winter.annotation.Component;
import com.project.winter.annotation.Configuration;
import com.project.winter.exception.bean.NoFindBeanByTypeException;
import com.project.winter.exception.bean.NoFindBeanByBeanNameException;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class BeanFactory {

    private static volatile BeanFactory instance;

    private String packagePrefix = "com.project.winter";
    private Reflections reflections;
    private final Map<BeanInfo, Object> beans = new HashMap<>();
    private final List<Object> configurations = new ArrayList<>();

    private BeanFactory() {}

    public static BeanFactory getInstance() {
        if (instance == null) initInstance();

        return instance;
    }

    public static void initInstance() {
        synchronized (BeanFactory.class) {
            if (instance == null) {
                instance = new BeanFactory();
                instance.initialize();
            }
        }
    }

    public void setTargetPackage(String packagePrefix) {
        BeanFactory.getInstance().packagePrefix = packagePrefix;
    }

    private void initialize() {
        reflections = new Reflections(packagePrefix);
        Set<Class<?>> preInstantiatedClazz = getClassTypeAnnotatedWith(Component.class);

        createBeansByConfiguration();
        createBeansByClass(preInstantiatedClazz);
    }

    private Set<Class<?>> getClassTypeAnnotatedWith(Class<? extends Annotation> annotation) {
        Set<Class<?>> types = new HashSet<>();

        reflections.getTypesAnnotatedWith(annotation).forEach(type -> {
            if (!type.isAnnotation() && !type.isInterface()) {
                if (type.isAnnotationPresent(Configuration.class)) configurations.add(createInstance(type));
                else types.add(type);
            }
        });

        return types;
    }

    private void createBeansByConfiguration() {
        configurations.forEach(getInstance()::createBeanInConfigurationAnnotatedClass);
    }

    private void createBeanInConfigurationAnnotatedClass(Object configuration) {
        Class<?> subclass = configuration.getClass();
        Map<Class<?>, Method> beanMethodNames = BeanFactoryUtils.getBeanAnnotatedMethodInConfiguration(subclass);

        beanMethodNames.forEach((clazz, method) -> {
            List<Object> parameters = new ArrayList<>();

            Arrays.stream(method.getParameters()).forEach(parameter -> {
                Class<?> parameterType = parameter.getType();
                String parameterName = parameter.getName();
                if (isBeanInitialized(parameterName, clazz)) parameters.add(getBean(parameterName, clazz));
                else parameters.add(createInstance(parameterType));
            });

            try {
                Object object = method.invoke(configuration, parameters.toArray());
                Bean anno = method.getAnnotation(Bean.class);
                String beanName = (anno.name().isEmpty()) ? method.getName() : anno.name();
                putBean(beanName, clazz, object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void createBeansByClass(Set<Class<?>> preInstantiatedClazz) {
        for (Class<?> clazz : preInstantiatedClazz) {
            if (isBeanInitialized(clazz.getSimpleName(), clazz)) continue;

            Object instance = createInstance(clazz);
            putBean(clazz.getName(), clazz, instance);
        }
    }

    private Object createInstance(Class<?> clazz) {
        Constructor<?> constructor = findConstructor(clazz);

        try {
            return constructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor<?> findConstructor(Class<?> clazz) {

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

    private boolean isBeanInitialized(String beanName, Class<?> clazz) {
        BeanInfo beanInfo = new BeanInfo(beanName, clazz);
        return beans.containsKey(beanInfo);
    }

    public <T> T getBean(String beanName, Class<T> clazz) {
        BeanInfo beanInfo = new BeanInfo(beanName, clazz);
        return (T) getBean(beanInfo);
    }

    public Object getBean(BeanInfo beanInfo) {
        return beans.get(beanInfo);
    }

    public Object getBean(String beanName) {
        BeanInfo beanInfo = beans.keySet().stream().filter(key -> key.isCorrespondName(beanName)).findFirst().orElseThrow(NoFindBeanByBeanNameException::new);
        return getBean(beanInfo);
    }

    public <T> T getBean(Class<T> clazz) {
        BeanInfo beanInfo = beans.keySet().stream().filter(key -> key.sameType(clazz)).findFirst().orElseThrow(NoFindBeanByTypeException::new);
        return (T) getBean(beanInfo);
    }

    private <T> void putBean(String beanName, Class<T> clazz, Object bean) {
        BeanInfo beanInfo = new BeanInfo(beanName, clazz);
        beans.put(beanInfo, bean);
    }

    public <T> Map<BeanInfo, T> getBeans(Class<T> type) {
        Map<BeanInfo, T> result = new HashMap<>();
        beans.forEach((key, value) -> {
            if (key.sameType(type) || key.isAssignableFrom(type)) result.put(key, (T) value);
        });

        return result;
    }

    public Map<BeanInfo, Object> getAnnotatedBeans(Class<? extends Annotation> annotation) {
        Map<BeanInfo, Object> result = new HashMap<>();
        beans.forEach((key, value) -> {
            if (key.isAnnotated(annotation)) result.put(key, value);
        });

        return result;
    }

}
