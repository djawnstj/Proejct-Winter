package com.project.winter.beans;

import com.project.winter.annotation.Autowired;
import com.project.winter.annotation.Bean;
import com.project.winter.annotation.Component;
import com.project.winter.annotation.Configuration;
import com.project.winter.config.WebMvcConfigurationSupport;
import com.project.winter.config.WebMvcConfigurer;
import com.project.winter.exception.bean.NoFindBeanByTypeException;
import com.project.winter.exception.bean.NoFindBeanByBeanNameException;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class BeanFactory {

    private static volatile BeanFactory instance;

    private String packagePrefix = "com.project.winter";
    private Reflections reflections;
    private Set<Class<?>> preInstantiatedClazz;
    private final Map<BeanInfo, Object> beans = new HashMap<>();

    protected final WebMvcConfigurationSupport configurationSupport = new WebMvcConfigurationSupport();

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
        preInstantiatedClazz = getClassTypeAnnotatedWith(Component.class);

        createBeansByClass(preInstantiatedClazz);

        preInstantiatedClazz = null;

        configurationSupport.loadConfigurer();
    }

    private Set<Class<?>> getClassTypeAnnotatedWith(Class<? extends Annotation> annotation) {
        return reflections.getTypesAnnotatedWith(annotation)
                .stream()
                .filter(type -> (!type.isAnnotation() && !type.isInterface()))
                .collect(Collectors.toSet());
    }

    private void createBeanInConfigurationAnnotatedClass(Object configuration) {
        Class<?> subclass = configuration.getClass();
        Map<Class<?>, Method> beanMethodNames = getBeanAnnotatedMethodInConfiguration(subclass);

        beanMethodNames.forEach((clazz, method) -> {
            final Object[] parameters = createParameters(method.getParameterTypes());

            try {
                Object object = method.invoke(configuration, parameters);
                Bean anno = method.getAnnotation(Bean.class);
                String beanName = (anno.name().isEmpty()) ? method.getName() : anno.name();
                putBean(beanName, clazz, object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Object[] createParameters(Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes).map(parameterType ->
            (isBeanInitialized(parameterType.getSimpleName(), parameterType)) ? getBean(parameterType.getSimpleName(), parameterType) : createInstance(parameterType)
        ).toArray();
    }

    private static Map<Class<?>, Method> getBeanAnnotatedMethodInConfiguration(Class<?> configuration) {
        Map<Class<?>, Method> instantMap = new HashMap<>();

        Arrays.stream(configuration.getMethods()).forEach(method -> {
            Class<?> returnType = method.getReturnType();
            if (returnType == void.class || instantMap.containsKey(returnType)) return;

            boolean isBeanMethod = Arrays.stream(method.getAnnotations()).anyMatch(anno -> anno.annotationType() == Bean.class);
            if (isBeanMethod) instantMap.put(returnType, method);
        });

        return instantMap;
    }

    private void createBeansByClass(Set<Class<?>> preInstantiatedClazz) {
        List<WebMvcConfigurer> configurers = new ArrayList<>();

        for (Class<?> clazz : preInstantiatedClazz) {
            if (isBeanInitialized(clazz.getSimpleName(), clazz)) continue;

            Object instance = createInstance(clazz);

            if (clazz.isAnnotationPresent(Configuration.class)) {
                createBeanInConfigurationAnnotatedClass(instance);

                if (WebMvcConfigurer.class.isAssignableFrom(instance.getClass())) configurers.add((WebMvcConfigurer) instance);
            }
        }

        configurationSupport.addWebMvcConfigurers(configurers);
    }

    private Object createInstance(Class<?> clazz) {
        if (!isDeclaredBean(clazz)) throw new IllegalArgumentException("Not declared bean: " + clazz.getSimpleName());

        Constructor<?> constructor = findConstructor(clazz);

        Set<Method> methods = getAutowiredMethodsInClass(clazz);

        Set<Field> fields = getAutowiredFieldsInClass(clazz);

        try {
            final Object[] parameters = createParameters(constructor.getParameterTypes());

            constructor.setAccessible(true);

            final Object instance = constructor.newInstance(parameters);

            injectAutowiredMethod(methods, instance);

            injectAutowiredFields(fields, instance);

            putBean(clazz.getName(), clazz, instance);

            return instance;

        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isDeclaredBean(final Class<?> clazz) {
        return (preInstantiatedClazz != null && preInstantiatedClazz.contains(clazz));
    }

    private void injectAutowiredFields(final Set<Field> fields, final Object instance) throws IllegalAccessException {
        for (final Field field : fields) {
            field.setAccessible(true);
            if (!isBeanInitialized(field.getType().getSimpleName(), field.getType())) createInstance(field.getType());

            field.set(instance, getBean(field.getType()));
        }
    }

    private void injectAutowiredMethod(final Set<Method> methods, final Object instance) throws IllegalAccessException, InvocationTargetException {
        for (final Method method : methods) {
            method.setAccessible(true);

            final Object[] parameters = createParameters(method.getParameterTypes());

            method.invoke(instance, parameters);
        }
    }

    private Set<Field> getAutowiredFieldsInClass(Class<?> clazz) {
        Set<Field> fields = new HashSet<>();

        fields.addAll(filterAutowiredFields(clazz.getFields()));
        fields.addAll(filterAutowiredFields(clazz.getDeclaredFields()));

        return fields;
    }

    private List<Field> filterAutowiredFields(Field[] clazz) {
        return Arrays.stream(clazz).filter(field -> field.isAnnotationPresent(Autowired.class)).toList();
    }

    private Set<Method> getAutowiredMethodsInClass(Class<?> clazz) {
        Set<Method> methods = new HashSet<>();

        methods.addAll(filterAutowiredMethods(clazz.getMethods()));
        methods.addAll(filterAutowiredMethods(clazz.getDeclaredMethods()));

        return methods;
    }

    private List<Method> filterAutowiredMethods(Method[] clazz) {
        return Arrays.stream(clazz).filter(method -> method.isAnnotationPresent(Autowired.class)).toList();
    }

    private Constructor<?> findConstructor(Class<?> clazz) {

        Set<Constructor<?>> allConstructors = getAllConstructors(clazz);

        if (allConstructors.size() == 1) return allConstructors.stream().findFirst().get();

        return findAutowiredConstructor(allConstructors);
    }

    private Set<Constructor<?>> getAllConstructors(Class<?> clazz) {
        Set<Constructor<?>> allConstructors = new HashSet<>();

        allConstructors.addAll(List.of(clazz.getDeclaredConstructors()));
        allConstructors.addAll(List.of(clazz.getConstructors()));

        return allConstructors;
    }


    private Constructor<?> findAutowiredConstructor(Set<Constructor<?>> allConstructors) {

        for (Constructor<?> constructor : allConstructors) {
            final boolean isAutowiredConstructor = constructor.isAnnotationPresent(Autowired.class);

            if (isAutowiredConstructor) return constructor;
        }

        return null;
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
