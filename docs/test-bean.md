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

Firstly, we create a test using `MockitoBean`.

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
> Starting with Spring Boot 3.4, the `MockBean` and `SpyBean` annotations are marked as `@Deprecated` and will be removed in a future version. It is recommended to use the new annotations provided in Spring Framework 6.2 instead.


