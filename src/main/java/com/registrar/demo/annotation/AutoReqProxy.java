package com.registrar.demo.annotation;

import com.registrar.demo.registrar.AutoRequestProxyRegistrar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，加了此注解的 bean 会被 {@link AutoRequestProxyRegistrar AutoRequestProxyRegistrar} 中的 scanner 扫描到
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoReqProxy {
}
