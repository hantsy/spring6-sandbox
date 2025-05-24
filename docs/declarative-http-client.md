# An Introduction to Spring Declarative HTTP Client

If you're familiar with [Feign Client](https://github.com/OpenFeign/feign), you probably love its declarative approach to creating HTTP/REST clients. Spring Cloud has embraced this too, integrating Feign into its ecosystem with [Spring Cloud OpenFeign](https://spring.io/projects/spring-cloud-openfeign).

Now, starting from **Spring 6**, the Spring Framework brings its own built-in declarative HTTP client mechanism. Unlike other solutions, it leverages existing tools like `WebClient`, `RestClient`, and the classic `RestTemplate`. This means you can create HTTP clients by simply defining a Java interface and using annotations.

---

## Declaring an HTTP Service with WebClient

Let’s look at how to create a *declarative* HTTP client using the reactive `WebClient`.

```java
@HttpExchange(url = "/posts", accept = "application/json", contentType = "application/json")
public interface PostClient {
    @GetExchange("")
    Flux<Post> allPosts();

    @GetExchange("/{id}")
    Mono<Post> getById(@PathVariable("id") UUID id);

    @PostExchange("")
    Mono<ResponseEntity<Void>> save(@RequestBody Post post);

    @PutExchange("/{id}")
    Mono<ResponseEntity<Void>> update(@PathVariable UUID id, @RequestBody Post post);

    @DeleteExchange("/{id}")
    Mono<ResponseEntity<Void>> delete(@PathVariable UUID id);
}
```

Here’s what’s happening:

- **`@HttpExchange`**: Marks the interface as an HTTP service and sets a base path (`/posts`) for all operations.
- **`@GetExchange`, `@PostExchange`, etc.**: Map methods to specific HTTP operations.
- **`@PathVariable` and `@RequestBody`**: Reuse familiar Spring Web annotations for handling parameters and request bodies.

This setup is similar to Spring’s `@RequestMapping` or `@GetMapping`, but with a cleaner and simplified API focused solely on HTTP service definitions.

---

## `Post` and `Status`

We’ll use a simple `Post` record type to represent the HTTP entity data.

```java
// Post.java
public record Post(UUID id,
       String title,
       String content,
       Status status,
       LocalDateTime createdAt
) {
}
```

And define the `Post` status as an `enum`.

```java
// Status.java
public enum Status {
    DRAFT,
    PENDING_MODERATION,
    PUBLISHED;
}
```

We configured Jackson to serialize/deserialize HTTP messages.

---

## Wiring the Client Together

To activate the `PostClient` interface, declare it as a Spring Bean using `HttpServiceProxyFactory`.

```java
@Configuration
public class ClientConfig {

    @Bean
    WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080")
                // .codecs...
                // .httpStatusHandler...
                .build();
    }

    @Bean
    PostClient postClient(WebClient webClient) {
        return HttpServiceProxyFactory.builder()
                .exchangeAdapter(WebClientAdapter.create(webClient))
                .build()
                .createClient(PostClient.class);
    }
}
```

The key points in the above configuration.

- **`WebClient`**: The base client used to make HTTP requests.
- **`HttpServiceProxyFactory`**: Bridges the interface (`PostClient`) and the underlying HTTP client (`WebClient`).
- **`.codec`**: The hook used to customize the HTTP messages encoding/decoding.
- **`.httpStatusHadler`**: The hook used to handle HTTP error status (and convert it to general exceptions) 
---

## Using the Client

Once `PostClient` is registered as a Spring Bean, you can inject it and use it like this:

```java
client.save(new Post(null, "test", "test content", Status.DRAFT, null))
    .log()
    .flatMap(saved -> {
        var uri = saved.getHeaders().getLocation().toString();
        var idString = uri.substring(uri.lastIndexOf("/") + 1);
        return client.getById(UUID.fromString(idString)).log();
    })
    .flatMap(post -> client.update(post.id(), 
            new Post(null, "updated test", "updated content", Status.PENDING_MODERATION, null)))
    .subscribe(
        response -> log.debug("Update status: {}", response)
    );
```

You can perform CRUD operations declaratively without manually handling HTTP requests. Just focus on your business logic!

---

## Testing the Client

### Using WireMock

To test your HTTP client, you can use [WireMock](https://wiremock.org/) to stub the external API. WireMock allows you to simulate HTTP responses and verify that your client behaves as expected. Here’s an example test for `PostClient`:

```java
@SpringJUnitConfig(ClientConfig.class)
@WireMockTest(httpPort = 8080)
public class PostClientTest {

    @Autowired
    PostClient postClient;

    @Test
    void testGetAllPosts() {
        stubFor(get("/posts")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("[{\"id\":\"123e4567-e89b-12d3-a456-426614174000\",\"title\":\"First Post\",\"content\":\"Hello World\",\"status\":\"DRAFT\",\"createdAt\":\"2025-05-01T10:00:00\"}]")));

        postClient.allPosts()
            .as(StepVerifier::create)
            .expectNextMatches(post -> post.title().equals("First Post"))
            .verifyComplete();

        verify(getRequestedFor(urlEqualTo("/posts")));
    }
}
```

Get the complete testing codes using Wiremock [here](https://github.com/hantsy/spring6-sandbox/blob/master/declarative-http-client/src/test/java/com/example/demo/PostClientTest.java).

### Using MockRestServiceServer

Alternatively, Spring offers its own testing utility called [MockRestServiceServer](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/client/MockRestServiceServer.html). This is a lightweight option for mocking HTTP interactions without external dependencies like WireMock.

---

## Wrapping Up

Spring's declarative HTTP client in **Spring 6** offers a modern, streamlined way to interact with REST APIs. It’s declarative, integrates seamlessly with existing Spring tools, and supports both reactive and non-reactive approaches.

If you’ve been using Feign or other declarative clients, you’ll feel right at home. And if you haven’t, this is the perfect time to explore the simplicity and power of declarative HTTP clients!

Get the [complete example codes](https://github.com/hantsy/spring6-sandbox/tree/master/declarative-http-client) on my GitHub. Feel free to fork and experiment. Happy coding!
