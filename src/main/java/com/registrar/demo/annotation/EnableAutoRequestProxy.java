package com.registrar.demo.annotation;

import com.registrar.demo.registrar.AutoRequestProxyRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启 auto http handler proxy，
 * 加了{code @AutoReqProxy} 注解的 component 会由{@link AutoRequestProxyRegistrar} 自动生成;
 *
 * 由于该注解只代理了 {@code @Import(AutoRequestProxyRegistrar.class)} 一个注解，所以在应用{@code @EnableAutoRequestProxy} 的地方，也可以直接应用 {@code @Import(AutoRequestProxyRegistrar.class)}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(AutoRequestProxyRegistrar.class)
public @interface EnableAutoRequestProxy {
}
