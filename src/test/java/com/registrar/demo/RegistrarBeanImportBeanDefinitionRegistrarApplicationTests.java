package com.registrar.demo;

import com.registrar.demo.bean.IRequestDemo;
import com.registrar.demo.http.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Optional;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RegistrarBeanImportBeanDefinitionRegistrarApplicationTests {
    @Autowired
    IRequestDemo iRequestDemo;

    @Test
    public void test1() {
        HttpResult<String> result = this.iRequestDemo.test1();
        String response = result.getResponse();
        log.info(">>>>>>>>>>{}", response);
        assertEquals("http request: url=http://abc.com and method=GET",response);

        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> urls = cl.getResources("META-INF/spring.provides");
            String res = Optional.ofNullable(urls).filter(Enumeration::hasMoreElements).map(Enumeration::nextElement).map(URL::toString).orElse("res empty.");
            log.info("res url:" + res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        HttpResult<String> result = this.iRequestDemo.test2();
        String response = result.getResponse();
        log.info(">>>>>>>>>>{}", response);
        assertEquals("http request: url=http://test2.com and method=POST",response);
    }

}
