package ru.ardyc.refwrapper.processor;

import ru.ardyc.refwrapper.exception.UnknownMethodException;
import ru.ardyc.refwrapper.WrapperFactory;
import ru.ardyc.refwrapper.annotation.Wrapped;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class MethodProcessor {

    public static <T> Object processMethod(Class<T> mainClass, Object mainObject, Method method, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName = method.getAnnotation(ru.ardyc.refwrapper.annotation.Method.class).value();
        if (Arrays.stream(mainClass.getMethods()).noneMatch(m -> m.getName().equals(methodName)))
            throw new UnknownMethodException(methodName);
        return internalProcessMethod(methodName, mainClass, mainObject, method, args);
    }

    public static <T> Object processDefaultMethod(Class<T> mainClass, Object mainObject, Method method, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return internalProcessMethod(method.getName(), mainClass, mainObject, method, args);
    }

    private static <T> Object internalProcessMethod(String methodName, Class<T> mainClass, Object mainObject, Method method, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class[] classes = null;
        if (args != null) {
            classes = new Class[args.length];
            for (int i = 0; i < classes.length; i++) {
                classes[i] = args[i].getClass();
            }
        }

        Method mainMethod = mainClass.getMethod(methodName, classes);
        if (!Modifier.isStatic(mainMethod.getModifiers()) && mainObject == null)
            throw new IllegalStateException("Method " + methodName + " is not static and cannot be used with null object");

        boolean isWrapped = method.getReturnType().isAnnotationPresent(Wrapped.class);

        if (!method.getReturnType().equals(mainMethod.getReturnType()) && !isWrapped)
            throw new IllegalStateException("Method " + methodName + " cannot be used with this wrapper, because return types are not same");

        Object obj = mainMethod.invoke(mainObject, args);
        if (obj == null)
            return null;

        if (isWrapped)
            return WrapperFactory.createWrapper(method.getReturnType(), obj);
        return obj;
    }

}
