package com.gmail.mooman219.pokemongo.bus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventHandlerMethod {

    private final Object listener;
    private final Method method;

    public void invoke(Object event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        method.invoke(listener, event);
    }

    public EventHandlerMethod(Object listener, Method method) {
        this.listener = listener;
        this.method = method;
    }

    public Object getListener() {
        return listener;
    }

    public Method getMethod() {
        return method;
    }
}
