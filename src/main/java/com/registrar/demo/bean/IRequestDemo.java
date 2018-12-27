package com.registrar.demo.bean;

import com.registrar.demo.annotation.AutoReqProxy;
import com.registrar.demo.annotation.HTTPMethod;
import com.registrar.demo.annotation.HTTPRequest;
import com.registrar.demo.http.HttpResult;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.stereotype.Component;

/**
 * {@code @Component } 把此类/接口注册成一个{@link BeanDefinition}， 由于加了{@code @AutoReqProxy} 被HTTPRequestRegistrar中的 scanner 扫描
 */
@AutoReqProxy
public interface IRequestDemo {

    @HTTPRequest(url = "http://abc.com")
    HttpResult<String> test1();

    @HTTPRequest(url = "http://test2.com", httpMethod = HTTPMethod.POST)
    HttpResult<String> test2();
}
