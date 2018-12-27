package com.registrar.demo.http;

import java.lang.reflect.Method;

public interface HTTPHandler {
    HttpResult<?> handle(Method method);
}
