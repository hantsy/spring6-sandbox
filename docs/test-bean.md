# Overriding Spring Beans with `@TestBean`

If you have been using Spring Boot since version 2.0, you might be familiar with the [`@MockBean`](https://docs.spring.io/spring-boot/api/java/org/springframework/boot/test/mock/mockito/MockBean.html) and [`@SpyBean`](https://docs.spring.io/spring-boot/api/java/org/springframework/boot/test/mock/mockito/SpyBean.html) annotations provided in the Spring Boot `test` starter. These annotations help isolate dependencies and test target beans in a mock environment.

With the release of Spring Framework 6.2, a new annotation, [`@TestBean`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/bean/override/convention/TestBean.html), has been introduced. This annotation allows you to override existing beans in the Spring test `ApplicationContext` using a static factory method or an alternative bean.

## TestBean

There are some conventions to follow when using `@TestBean` in your projects:

* The `@TestBean` annotation can only be applied to non-static fields in the test class.
* The `@TestBean` can only override `singleton` beans. Attempting to apply it to beans with other scopes will result in exceptions. If the bean is created from a `BeanFactory`, it will replace the `BeanFactory` at runtime.

By default, `@TestBean` will look up the candidate bean by type that is compatible with the annotated field.

By setting the `methodName` property explicitly, you can specify the static method name in the test class or a derived class. For example:

```java
@TestBean(methodName="dummyCustomerService")
CustomerService externalCustomerService;

//...

static CustomerService dummyCustomerService(){
    return new DummyCustomerService();
}
```
> [!NOTE]
> The factory method must be a no-argument method.

Or you can specify a static factory method from an external class using the format `<class FQN>#<method name>`. For example:

```java
@TestBean(methodName="com.example.TestUtils#dummyCustomerService")
CustomerService externalCustomerService;
```
Alternatively, you can specify an alternative bean by setting the `name` or `value` property of the `@TestBean` annotation.

> [!NOTE]
> Note that the `name` or `value` property can also accept a factory method name, which might be a bit confusing.

If you set the `enforceOverride` property to `true`, and no corresponding beans are found in the context, an exception will be thrown.

## MockitoBean and MockitoSpyBean

Spring Framework 6.2 introduces two additional annotations similar to `@TestBean`:

* `@MockitoBean` - a replacement for Spring Boot's `@MockBean`
* `@MockitoSpyBean` - a replacement for Spring Boot's `@SpyBean`

Assume there is a `CustomerService` interface that includes two methods:

```java
public interface CustomerService {
    Customer findByEmail(String email);
    List<Customer> findAll();
}
```

Here is a simple class that implements this interface.

```java
public class DefaultCustomerService implements CustomerService {
    @Override
    public Customer findByEmail(String email) {
        return new Customer("foo", "bar", "foobar@example.com");
    }

    @Override
    public List<Customer> findAll() {
        return List.of(
                new Customer("foo", "bar", "foobar@example.com"),
                new Customer("foo2", "bar2", "foobar2@example.com")
        );
    }
}
```

We declare the implementation class as a `Bean` in the configuration class.

```java
@Configuration
public class Config {

    @Bean
    public CustomerService customerService() {
        return new DefaultCustomerService();
    }
}
```

Firstly, we create a test using `@MockitoBean`.

```java
@SpringJUnitConfig(classes = Config.class)
class CustomerServiceMockitoTest {

    @MockitoBean
    CustomerService customerServiceMock;

    @Test
    public void testCustomerService() {
        when(customerServiceMock.findByEmail("dummy@example.com"))
                .thenReturn(
                        new Customer("dummy first", "dummy last", "dummy@example.com")
                );
        when(customerServiceMock.findAll()).thenReturn(Collections.emptyList());

        // test bean
        var testCustomer = customerServiceMock.findByEmail("dummy@example.com");
        assertThat(testCustomer.firstName()).isEqualTo("dummy first");
        assertThat(testCustomer.lastName()).isEqualTo("dummy last");
        assertThat(customerServiceMock.findAll().size()).isEqualTo(0);

        verify(customerServiceMock, times(1)).findByEmail(anyString());
        verify(customerServiceMock, times(1)).findAll();
        verifyNoMoreInteractions(customerServiceMock);
    }
}
```

When applying `@MockitoBean` to `CustomerService` and stubbing the behaviors in the bean, it replaces the behaviors in the default implementation.

Now, let's look at the usage of `@MockitoSpyBean`.

```java
@SpringJUnitConfig(classes = Config.class)
class CustomerServiceMockitoSpyTest {
    // have to specify the bean name if variable name is not matched the bean name
    @MockitoSpyBean(name = "customerService")
    CustomerService customerServiceSpy;

    @Test
    public void testCustomerService() {
        when(customerServiceSpy.findByEmail("dummy@example.com"))
                .thenReturn(
                        new Customer("dummy first", "dummy last", "dummy@example.com")
                );

        // test bean
        var testCustomer = customerServiceSpy.findByEmail("dummy@example.com");
        assertThat(testCustomer.firstName()).isEqualTo("dummy first");
        assertThat(testCustomer.lastName()).isEqualTo("dummy last");
        assertThat(customerServiceSpy.findAll().size()).isEqualTo(2);

        verify(customerServiceSpy, times(1)).findByEmail(anyString());
        verify(customerServiceSpy, times(1)).findAll();
        verifyNoMoreInteractions(customerServiceSpy);
    }
}
```

In this test, we partially stub the methods, leaving the `findAll` method unstubbed. When running the tests, it will invoke the real `findAll` method of the default implementation class.

> [!NOTE]
> Starting with Spring Boot 3.4, the `@MockBean` and `@SpyBean` annotations are marked as `@Deprecated` and will be removed in a future version. It is recommended to use the new annotations provided in Spring Framework 6.2 instead.

## Building Your Own Bean Overriding Strategy

If you explore the source code of the `@TestBean`, `@MockitoBean`, and `@MockitoSpyBean` annotations, you will find that they are all meta-annotations of `@BeanOverride`. This annotation accepts a parameter to specify a `BeanOverrideProcessor` that handles these annotations at runtime.

As an example, we will create a simple `@StubBean` annotation to replace the real bean with a stub class in the testing codes.

Firstly, define a new annotation, `@StubBean`, that uses `@BeanOverride` to specify a custom `BeanOverrideProcessor`.

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanOverride(StubBeanOverrideProcessor.class)
public @interface StubBean {
    Class<?> value();
}
```

Then create a `StubBeanOverrideProcessor` class that implements `BeanOverrideProcessor` and handles the custom annotation.

```java
class StubBeanOverrideProcessor implements BeanOverrideProcessor {
    @Override
    public BeanOverrideHandler createHandler(Annotation overrideAnnotation, Class<?> testClass, Field field) {
        if (overrideAnnotation instanceof StubBean stubBean) {
            return new StubBeanOverrideHandler(stubBean, field, ResolvableType.forField(field, testClass));
        }
        throw new IllegalArgumentException("Make sure the bean to override is annotated with @StubBean");
    }
}
```

Implement a `StubBeanOverrideHandler` class that extends `BeanOverrideHandler` to create and track the override instance.

```java
public class StubBeanOverrideHandler extends BeanOverrideHandler {
    private static Logger log = LoggerFactory.getLogger(StubBeanOverrideHandler.class);
    private StubBean stubBean;

    public StubBeanOverrideHandler(StubBean stubBean, Field field, ResolvableType resolvableType) {
        super(field, resolvableType, null, BeanOverrideStrategy.REPLACE_OR_CREATE);
        this.stubBean = stubBean;
    }

    @SneakyThrows
    @Override
    protected Object createOverrideInstance(String beanName, BeanDefinition existingBeanDefinition, Object existingBeanInstance) {
        // create a stub object...
        return stubBean.value().getDeclaredConstructor().newInstance();
    }

    @Override
    protected void trackOverrideInstance(Object override, SingletonBeanRegistry singletonBeanRegistry) {
        log.debug("track override instance, override: {}, singleton bean registry: {}", override, singletonBeanRegistry);
    }
}
```

Do not forget to register the `StubBeanOverrideProcessor` as a Spring bean in a configuration class.

```java
@Configuration
public class CustomConfig {

    @Bean
    public BeanOverrideProcessor stubBeanOverrideProcessor() {
        return new StubBeanOverrideProcessor();
    }
}
```

Lastly, create tests to verify the functionality of the custom bean overriding rule using the new `@StubBean` annotation.

```java
@SpringJUnitConfig(classes = {Config.class, CustomConfig.class})
class CustomerServiceStubBeanTest {

    @StubBean(DummyCustomerService.class)
    CustomerService testCustomerService;

    @Test
    public void testCustomerService() {
        var testCustomer = testCustomerService.findByEmail("dummy@example.com");
        assertThat(testCustomer.firstName()).isEqualTo("dummy first");
        assertThat(testCustomer.lastName()).isEqualTo("dummy last");
        assertThat(testCustomerService.findAll().size()).isEqualTo(0);
    }
}
```

> [!NOTE]
> In a real-world project, you can use a fixture data generation library to easily create dummy stub classes for testing purposes.

Check out the complete [example project](https://github.com/hantsy/spring6-sandbox/tree/master/test-bean) from my Github account and explore the source codes yourself.