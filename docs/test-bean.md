# Overriding Spring Beans with `@TestBean`

If you have used Spring Boot since 2.0, you should be impressed by the [`@MockBean`](https://docs.spring.io/spring-boot/api/java/org/springframework/boot/test/mock/mockito/MockBean.html) and [`@SpyBean`](https://docs.spring.io/spring-boot/api/java/org/springframework/boot/test/mock/mockito/SpyBean.html) provided in the Spring Boot `test` starter, which help you to isolate the dependencies and test the target beans in a mock environment.

Spring Framework 6.2 introduces a new annotation [`@TestBean`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/bean/override/convention/TestBean.html) which allows you to override the existing beans in the Spring test `ApplicationContext` with a static factory method or an alternative bean.

## TestBean

There are some conventions when using `@TestBean` in your projects.

* The `@TestBean` can only be annotated on a non-static field in the test class.
* The `@TestBean` can only override `singleton` beans. If you try to apply it to other scoped beans, it will throw exceptions. And if the bean is created from a `BeanFactory`, it will replace the `BeanFactory` at runtime.

By default, `@TestBean` will look up the candidate bean by type that is compatible with the annotated field.

By setting the `methodName` property explicitly, you can specify the static method name in the test class or derived class. For example:

```java
@TestBean(methodName="dummyCustomerService")
CustomerService externalCustomerService;

//...

static CustomerService dummyCustomerService(){
    return new DummyCustomerService();
}
```

> [!NOTE]
> The factory method should be a no-arguments method.

Or you can specify a static factory method from an external class with the form `<class FQN>#<method name>`, for example:

```java
@TestBean(methodName="com.example.TestUtils#dummyCustomerService")
CustomerService externalCustomerService;
```

Alternatively, you can specify an alternative bean by setting the `name` or `value` property.

If you set `enforceOverride` to `true`, and there are no corresponding beans in the context, it will throw exceptions.

## MockitoBean and MockitoSpyBean

Spring framework 6.2 provides two meta annotations based on the `TestBean`.

* `MockitoBean` - the replacement of the Spring Boot's `MockBean`
* `MockitoSpyBean` - the replacement of the Spring Boot's `SpyBean` 

Assume there is a `CustomerService` interface which includes two methods.

```java
public interface CustomerService {
    Customer findByEmail(String email);
    List<Customer> findAll();
}
```

And a simple class implements this interface.

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

We declare the implemenation class as a `Bean` in the config class.

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

As you see, when applying `@MockitoBean` on `CustomerService`, and stubing the behivors in the bean, it relace the behivors in the default implementation.

Let's have a look at the usage of `@MockitoSpyBean`.

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

In this test, we stub the methods partially, not stub the `findAll` method. When running the tests, it will call the real `findAll` method of the default implementation class.

> [!NOTE]
> Since Spring Boot 3.4, the `MockBean` and `SpyBean` are marked as `@Deprecated` which will be remove in a future version, it is better to use the new annotations provided in Spring 6.2 instead.


