package ru.ardyc.refwrapper.processor;

import ru.ardyc.refwrapper.exception.UnknownFieldException;
import ru.ardyc.refwrapper.exception.UnknownMethodException;
import ru.ardyc.refwrapper.WrapperFactory;
import ru.ardyc.refwrapper.annotation.Wrapped;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class FieldProcessor {

    public static <T> Object processField(Class<T> mainClass, Object mainObject, Method method, Object[] args) throws NoSuchFieldException, IllegalAccessException {
        String fieldName = method.getAnnotation(ru.ardyc.refwrapper.annotation.Field.class).value();
        if (Arrays.stream(mainClass.getFields()).noneMatch(m -> m.getName().equals(fieldName)))
            throw new UnknownFieldException(fieldName);

        Field field = mainClass.getField(fieldName);
        if (!Modifier.isStatic(field.getModifiers()) && mainObject == null)
            throw new IllegalStateException("Field " + field + " is not static and cannot be used with null object");

        if (method.getAnnotation(ru.ardyc.refwrapper.annotation.Field.class).isSetter()) {
            if (args.length != 1)
                throw new IllegalStateException("Method requires a single parameter");
            field.set(mainObject, args[0]);
            return null;
        }

        boolean isWrapped = method.getReturnType().isAnnotationPresent(Wrapped.class);

        if (!method.getReturnType().equals(field.getType()) && !isWrapped)
            throw new IllegalStateException("Field " + field + " cannot be used with this wrapper, because types are not same");
        Object obj = field.get(mainObject);
        if (isWrapped)
            return WrapperFactory.createWrapper(method.getReturnType(), obj);
        return obj;
    }

}
