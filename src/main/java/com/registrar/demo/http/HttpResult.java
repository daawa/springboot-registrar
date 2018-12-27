package com.registrar.demo.http;

public interface HttpResult<T> {

    T getResponse();

    int getStatus();
}
