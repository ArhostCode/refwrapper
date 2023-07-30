package ru.ardyc.refwrapper;


import ru.ardyc.refwrapper.exception.UnknownBehaviorException;
import ru.ardyc.refwrapper.processor.FieldProcessor;
import ru.ardyc.refwrapper.processor.MethodProcessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Objects;

public class WrapperFactory {

    public static <T> T createWrapper(Class<T> wrapper, Object object) {
        Objects.requireNonNull(wrapper);
        ClassLoader loader = object.getClass().getClassLoader();
        T obj = (T) Proxy.newProxyInstance(loader, new Class[]{wrapper}, (proxy, method, args) -> processProxy(object.getClass(), object, method, args));
        return obj;
    }

    public static <T> T createWrapper(Class<T> wrapper, ClassLoader loader, String objectName) throws ClassNotFoundException {
        Objects.requireNonNull(wrapper);
        Class clazz = loader.loadClass(objectName);
        T obj = (T) Proxy.newProxyInstance(loader, new Class[]{wrapper}, (proxy, method, args) -> processProxy(clazz, null, method, args));
        return obj;
    }

    private static <T> Object processProxy(Class<T> mainClass, Object mainObject, Method method, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        if (Arrays.stream(Object.class.getMethods()).anyMatch(m -> m.getName().equals(method.getName())))
            return MethodProcessor.processDefaultMethod(mainClass, mainObject, method, args);
        if (method.isAnnotationPresent(ru.ardyc.refwrapper.annotation.Method.class))
            return MethodProcessor.processMethod(mainClass, mainObject, method, args);
        if (method.isAnnotationPresent(ru.ardyc.refwrapper.annotation.Field.class))
            return FieldProcessor.processField(mainClass, mainObject, method, args);

        throw new UnknownBehaviorException(method.getName());
    }

}
