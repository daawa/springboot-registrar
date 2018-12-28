> 
> `ImportBeanDefinitionRegistrar` 通过`@Import`应用在 注解了 `@Configuration` 的类上。
>
>  Interface to be implemented by types that register additional bean definitions when
>  processing `@Configuration` classes. Useful when operating at the bean definition
>  level (as opposed to `@Bean` method/instance level) is desired or necessary.


### 使用 `ImportBeanDefinitionRegistrar` 动态注册bean

### ImportBeanDefinitionRegistrar

Spring官方在动态注册bean时，大部分套路其实是使用**`ImportBeanDefinitionRegistrar`**接口。

所有实现了该接口的类的都会被**`ConfigurationClassPostProcessor`**处理，

`ConfigurationClassPostProcessor`实现了**`BeanFactoryPostProcessor`**接口，

所以`ImportBeanDefinitionRegistrar`中**动态注册的bean是优先于对其有依赖的bean初始化的**，也能被aop、validator等机制处理。

##### 使用方法

**`ImportBeanDefinitionRegistrar`**需要配合`@Configuration`和`@Import`注解，

`@Configuration` 定义Java格式的Spring配置文件，

`@Import` 应用在在注解了`@Configuration`的类上，可以导入 **`ImportBeanDefinitionRegistrar`** 接口的实现类。

##### DEMO：实现类似 retrofit 的自动代理 bean

要实现的效果如下，在接口上使用注解定义url、http方法类型等信息，程序根据这些信息动态生成实现类

```java
@Component
@AutoReqProxy
public interface IRequestDemo {
    @HTTPRequest(url = "http://abc.com")
    HttpResult<String> test1();
    
    @HTTPRequest(url = "http://test2.com", httpMethod = HTTPMethod.POST)
    HttpResult<String> test2();
}
```

ref: [https://github.com/xwjie/MyRestUtil](http://link.zhihu.com/?target=https%3A//github.com/xwjie/MyRestUtil)

##### 例子编写步骤

1. 首先编写核心**ImportBeanDefinitionRegistrar**接口，重要代码如下：
    
    主要思路是利用**`ClassPathScanningCandidateComponentProvider`**获取标注了**`@AutoReqProxy`**注解的接口，并使用 ***JDK动态代理*** 为其生成代理对象。
    
    然后使用**`DefaultListableBeanFactory`**将代理对象注册到容器中。
    
    ```java
    
    /**
     *  dynamically register beans for interfaces that annotated by @AutoReqProxy
     */
    @Slf4j
    public class AutoRequestProxyRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware, BeanFactoryAware {
    
        private ClassLoader    classLoader;
        private ResourceLoader resourceLoader;
        private Environment    environment;
        private BeanFactory    beanFactory;
    
        @Override
        public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
            registerHttpRequest(beanDefinitionRegistry);
        }
    
        /**
         * @param beanDefinitionRegistry app-scope beanFactory
         */
        private void registerHttpRequest(BeanDefinitionRegistry beanDefinitionRegistry) {
            ClassPathScanningCandidateComponentProvider classScanner = getClassScanner();
            classScanner.setResourceLoader(this.resourceLoader);
    
            //only scan interfaces that annotated by @AutoReqProxy
            AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(AutoReqProxy.class);
    
            classScanner.addIncludeFilter(annotationTypeFilter);
            String basePack = "com.registrar.demo";
            Set<BeanDefinition> beanDefinitionSet = classScanner.findCandidateComponents(basePack);
    
            for (BeanDefinition beanDefinition : beanDefinitionSet) {
                if (beanDefinition instanceof AnnotatedBeanDefinition) {
                    registerBeans(((AnnotatedBeanDefinition) beanDefinition));
                }
            }
        }
    
        
        private void registerBeans(AnnotatedBeanDefinition annotatedBeanDefinition) {
            String className = annotatedBeanDefinition.getBeanClassName();
            ((DefaultListableBeanFactory) this.beanFactory).registerSingleton(className, createProxy(annotatedBeanDefinition));
        }
    
        /**
         * 构造Class扫描器，设置了只扫描顶级接口，不扫描内部类
         */
        private ClassPathScanningCandidateComponentProvider getClassScanner() {
            return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
    
                @Override
                protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                    if (beanDefinition.getMetadata().isInterface()) {
                        try {
                            Class<?> target = ClassUtils.forName(beanDefinition.getMetadata().getClassName(), classLoader);
                            return !target.isAnnotation();
                        } catch (Exception ex) {
                            log.error("load class exception:", ex);
                        }
                    }
                    return false;
                }
            };
        }
    
        
        private Object createProxy(AnnotatedBeanDefinition annotatedBeanDefinition) {
            try {
                AnnotationMetadata annotationMetadata = annotatedBeanDefinition.getMetadata();
                Class<?> target = Class.forName(annotationMetadata.getClassName());
                InvocationHandler invocationHandler = createInvocationHandler();
                Object proxy = Proxy.newProxyInstance(HTTPRequest.class.getClassLoader(), new Class[]{target}, invocationHandler);
                return proxy;
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage());
            }
            return null;
        }   
    
        ...
     
    }
    ```

2. 编写注解，并在其中使用`@Import`导入第1步编写的 `AutoRequestProxyRegistrar`。
    
    ```java
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Import(HTTPRequestRegistrar.class)
    public @interface EnableAutoRequestProxy {
    }
    ```

3. 将`@EnableAutoRequestProxy`添加到`@Configuration`注解下
   
   > 如果使用了Spring-Boot，由于`@SpringBootApplication`注解包含了`@Configuration`注解，可以将`@EnableAutoRequestProxy`添加到`@SpringBootApplication`注解下。
    
    ```java
    @SpringBootApplication
    @EnableHttpUtil
    public class RegistrarBeanImportBeanDefinitionRegistrarApplication {   
        public static void main(String[] args) {   
            SpringApplication.run(RegisterbeanImportBeanDefinitionRegistrarApplication.class, args);
        }
    }
    ```

4. 使用，直接注入 `IRequestDemo` 即可
    
    ```java
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
       }
    
       @Test
       public void test2() {
           HttpResult<String> result = this.iRequestDemo.test2();
           String response = result.getResponse();
           log.info(">>>>>>>>>>{}", response);
           assertEquals("http request: url=http://test2.com and method=POST",response);
       }
    
    }
    ```
    
