# Testing Spring WebMvc Applications with Assertj aware MockMvcTester

Since Spring 6.2, it introduces a new API - [`MockMvcTester`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/servlet/assertj/MockMvcTester.html) to test WebMvc controllers. `MockMvcTester` is based on the existing `MockMvc` but integrated tightly with the AssertJ fluent Assertions APIs, which allow developers to assert the HTTP response with AssertJ-style codes.

In the tests, it is easy to create a `MockMvcTester` instance with Spring `ApplicationContext`, controllers or the existing `MockMvc`. 

The following is an example of creating a `MockMvcTester` with `ApplicatoinContext`, and configure the underlay `MockMvc` with a `Function<MockMvcBuilder,MockMvc>` parameter.

```java
MockMvcTester.from(ctx, builder ->
		builder.addDispatcherServletCustomizer(dispatcherServlet ->
				dispatcherServlet.setEnableLoggingRequestDetails(true)
		)
		.build()
	)
``` 

The `dispatcherServlet.setEnableLoggingRequestDetails(true)` enables logging for request details, it is very useful to debug the issues in the request/response progress.

