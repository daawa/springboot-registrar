package com.registrar.demo.http;

import com.registrar.demo.annotation.HTTPRequest;

import java.lang.reflect.Method;

/**
 * proxy delegate
 */
public class DemoHttpHandler implements HTTPHandler {

    @Override
    public HttpResult<?> handle(Method method) {
        HTTPRequest request = method.getAnnotation(HTTPRequest.class);
        String url = request.url();
        String methodName = request.httpMethod().name();
        String str = String.format("http request: url=%s and method=%s", url, methodName);
        return new StringHttpResult(str, 200);
    }
}
