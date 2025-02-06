# Testing Spring WebMvc Applications with Assertj aware MockMvcTester

Since Spring 6.2, it introduced a new API - [`MockMvcTester`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/servlet/assertj/MockMvcTester.html) to test WebMvc controllers. `MockMvcTester` is based on the existing `MockMvc` but integrated tightly with the AssertJ fluent Assertions APIs, which allow developers to assert the HTTP response with AssertJ-style codes.

There are several options for creating a `MockMvcTester` instance in your testing codes. 

* `MockMvcTester.from(...)` is used to create a `MockMvcTester` instance from `ApplicationContext`.
* `MockMvcTester.create(...)` allows developers to create a `MockMvcTester` instance from the existing `MockMvc`.
* `MockMvcTester.of(...)` can specify certain controllers you want to test with the created `MockMvcTester` instance here.

The following is an example of creating a `MockMvcTester` with Spring `ApplicatoinContext`, and configuring the underlay `MockMvc` with a `Function<MockMvcBuilder, MockMvc>` parameter.

```java
MockMvcTester.from(ctx, builder ->
		builder.addDispatcherServletCustomizer(dispatcherServlet ->
				dispatcherServlet.setEnableLoggingRequestDetails(true)
		)
		.build()
	)
``` 

The `dispatcherServlet.setEnableLoggingRequestDetails(true)` enables logging for request details, it is very useful to debug the issues in the request/response progress.

Assume you have created a Spring WebMvc project and exposed the following RESTful APIs through controllers.
* `GET /api/posts` - Get all posts.
* `GET /api/posts/{id}` - Get post by id, if not found, return a 404 error.

> [!NOTE]
> We are focusing on the usage of `MockMvcTester` in this post, explore the [`PostRestController`](https://github.com/hantsy/spring6-sandbox/blob/master/mvc-freemarker/src/main/java/com/example/demo/web/PostRestController.java) source code that produces these APIs yourself if you are interested in it.

The following testing codes cover the *get all posts* case.

```java
@Test
public void getGetAllPosts() throws Exception {
	when(this.posts.findAll())
		.thenReturn(List.of(
				Post.of("test", "content of test1"),
				Post.of("test2", "content of test2")
			)
		);
	
	assertThat(this.mvc.perform(get("/api/posts").accept(MediaType.APPLICATION_JSON)))
		.hasStatusOk()
		.bodyJson()
		.hasPathSatisfying("$.size()", v -> v.assertThat().isEqualTo(2))
		.extractingPath("$[0].title").isEqualTo("test");
	
	verify(this.posts, times(1)).findAll();
	verifyNoMoreInteractions(this.posts);
}
```

The `MockMvcTester.perform()` will delegate the building request progress to a `RequestBuilder` and send it to the mocking Servlet engine, it returns a AssertJ aware `MvcTestResult`. `MockMvcTester` also provides several convenient methods mapped to the popular HTTP methods, including `get`, `post`, `put`, `delete`, `head`, `options`, etc., which returns a `MockMvcRequestBuilder`. The `MockMvcRequestBuilder.exchange()` will return a `MvcTestResult`.

With `assertThat(MvcTestResult)` or `assertThat(MockMvcRequestBuilder)`, we can assert the HTTP response result, including status, headers, body, etc.

In the above code snippets, we checked if the status and body content of the HTTP response were returned as expected. Most of the time, we do not compare the whole response content, using JsonPath to evaluate the value of a JSON node is the simplest way to verify the JSON data.

Let's move to another test case - *get post by id*.

```java
@Test
public void getGetPostById() {
	when(this.posts.findById(any(UUID.class)))
		.thenReturn(
			Optional.of(Post.of("test", "content of test1"))
		);
	
	assertThat(this.mvc.get().uri("/api/posts/{id}", UUID.randomUUID()).accept(MediaType.APPLICATION_JSON))
		.hasStatusOk()
		.bodyJson().hasPath("$.title");
	
	verify(this.posts, times(1)).findById(any(UUID.class));
	verifyNoMoreInteractions(this.posts);
}
```

Instead, here we use the `MockMvcTester.get()` to build a request, and check the existence of a JSON node by `bodyJson().hasPath("$.title")`.

Next, let's have a look at testing codes for the *post not found* case.

```java
@Test
public void getGetPostByIdNotFoundException() {
	when(this.posts.findById(any(UUID.class))).thenReturn(Optional.empty());
	assertThat(this.mvc.perform(get("/api/posts/{id}", UUID.randomUUID()).accept(MediaType.APPLICATION_JSON)))
		.hasStatus4xxClientError()
		// throws PostNotFoundException
		.hasFailed().failure()
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("not found");
	
	verify(this.posts, times(1)).findById(any(UUID.class));
	verifyNoMoreInteractions(this.posts);
}
```
Here we can check if the exception is the expected one handled by controller advice.

> [!NOTE]
>  Check the exception handler for `PostNotFoundException` [here](https://github.com/hantsy/spring6-sandbox/blob/master/mvc-freemarker/src/main/java/com/example/demo/web/PostRestController.java#L78).

For more details of using `MockMvcTester`, check the javadoc of [`MockMvcTester`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/servlet/assertj/MockMvcTester.html) and [`MvcTestReult`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/servlet/assertj/MvcTestResult.html) and [`MockMvcRequestBuilder`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/servlet/assertj/MockMvcTester.MockMvcRequestBuilder.html).

The [complete testing example](https://github.com/hantsy/spring6-sandbox/blob/master/mvc-freemarker/src/test/java/com/example/demo/web/PostRestControllerTestWithMockMvcTester.java) is available on my GitHub.
