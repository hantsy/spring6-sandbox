# An introduction to Spring RestClient API

Spring Framework 6.1 introduces a new synchronous HTTP client - [`RestClient`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestClient.html). It is built on top of the existing [`RestTemplate`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html) but offers a set of modern, fluent APIs for sending HTTP requests. 

> [!NOTE]
> If you have experience with [`WebClient`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html) from the Spring WebFlux module, think of `RestClient` as its blocking counterpart.

`RestClient` provides several convenient methods to create an instance quickly:

* `RestClient.create()` - Creates an instance with the default configuration.
* `RestClient.baseUrl(String baseUrl)` - Sets up a base URL to be connected.
* `RestClient.create(RestTemplate restTemplate)` - Reuses the settings in the existing `RestTemplate`.

Alternatively, it also provides a convenient `builder()` method to get a `RestClient.Builder` that can be used to customize common properties when initializing a `RestClient` instance, such as:

* The default URI (via `baseUrl(String baseUrl)`).
* Default headers and cookies.
* The underlying HTTP client engine (via `requestFactory(ClientHttpRequestFactory requestFactory)`).
* Message converters used to encode/decode HTTP message payloads (via `messageConverters(Consumer<List<HttpMessageConverter<?>>> converters)`).
* HTTP client interceptors to filter client requests/responses globally.

The following code uses `RestClient.Builder` to declare a `RestClient` bean in a Spring `Configuration` class:

```java
@Bean
RestClient restClient(ObjectMapper objectMapper) {
    return RestClient.builder()
            .baseUrl("http://localhost:8080")
            .messageConverters(converters -> {
                        converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
                    }
            )
            .requestFactory(new JdkClientHttpRequestFactory())
            .build();
}
```

`RestClient` can interact with remote third-party HTTP/REST APIs and is also useful for lightweight service-to-service communication in a microservice architecture.

To demonstrate how to use `RestClient` in real-world Spring projects, let's assume a collection of REST APIs served at `http://localhost:8080` that provide the following functionalities:

* `GET /posts` - Get all posts.
* `POST /posts` - Create a new post, return a 201 status, and set the new URI in the `Location` header.
* `GET /posts/{id}` - Get post by ID, if not found returns a 404 status.
* `PUT /posts/{id}` - Update a post.
* `DELETE /posts/{id}` - Delete a post by ID.

To interact with the above APIs, you can create a `PostClient` bean as shown below:

```java
@Bean
public PostClient postClient(RestClient restClient) {
        return new PostClient(restClient);
}
```

This bean can then be used to perform CRUD operations on the `Post` resources as demonstrated in the `PostClient` class.

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class PostClient {
    private final RestClient restClient;

    List<Post> allPosts() {
        return restClient.get().uri("/posts").accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    Post getById(UUID id) {
        var response = restClient.get().uri("/posts/{id}", id).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus((HttpStatusCode s) -> s == HttpStatus.NOT_FOUND,
                        (HttpRequest req, ClientHttpResponse res) -> {
                            throw new PostNotFoundException(id);
                        }
                )
                .toEntity(Post.class);
        log.debug("response status code: {}", response.getStatusCode());
        return response.getBody();
    }

    void save(Post post) {
        var response = restClient.post().uri("/posts").contentType(MediaType.APPLICATION_JSON).body(post)
                .retrieve()
                .toBodilessEntity();

        log.debug("saved location:" + response.getHeaders().getLocation());
    }

    void update(UUID id, Post post) {
        var response = restClient.put().uri("/posts/{id}", id).contentType(MediaType.APPLICATION_JSON).body(post)
                .retrieve()
                .toBodilessEntity();

        log.debug("updated post status:" + response.getStatusCode());
    }

    void delete(UUID id) {
        var response = restClient.delete().uri("/posts/{id}", id).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity();

        log.debug("deleted post status:" + response.getStatusCode());
    }
}
```

In the above code, we use the `RestClient` bean to interact with the APIs we defined earlier.

Firstly, `RestClient` calls methods like `get`, `post`, `put`, or `delete` to set the HTTP method, then calls `uri`, `header`, `accept`, `contentType`, etc., to prepare the HTTP request content, and finally calls `retrieve` or `exchange` to send the request.
* The `retrieve` method returns a `ResponseSpec` that makes it easier to extract the HTTP response body and headers or the entire HTTP response entity.
* The `exchange` method provides more control over the raw HTTP request and response data.

If the remote APIs are not accessible or not ready while you are building the client code, you can use [`MockRestServiceServer`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/client/MockRestServiceServer.html), [`WireMock`](https://wiremock.org/), or [Spring Cloud Contract](https://spring.io/projects/spring-cloud-contract) to mock the remote APIs and verify the client functionality in an isolated environment.

The following is an example using `WireMock` to set up a mock environment that serves the remote APIs in the testing code.

```java
@SpringJUnitConfig(
        classes = {
                ClientConfig.class,
                Jackson2ObjectMapperConfig.class,
                PostClient.class
        }
)
@WireMockTest(httpPort = 8080)
public class PostClientTest {

    static {
        ObjectMapper wireMockObjectMapper = Json.getObjectMapper();
        wireMockObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        wireMockObjectMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        wireMockObjectMapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);

        JavaTimeModule module = new JavaTimeModule();
        wireMockObjectMapper.registerModule(module);
    }

    @Autowired
    PostClient postClient;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testGetAllPosts() {
        var data = List.of(
                new Post(UUID.randomUUID(), "title1", "content1", Status.DRAFT, LocalDateTime.now()),
                new Post(UUID.randomUUID(), "title2", "content2", Status.PUBLISHED, LocalDateTime.now())
        );
        stubFor(get("/posts")
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withResponseBody(Body.fromJsonBytes(Json.toByteArray(data)))
                )
        );

        var posts = postClient.allPosts();
        assertThat(posts.size()).isEqualTo(2);

        verify(getRequestedFor(urlEqualTo("/posts"))
                .withHeader("Accept", equalTo("application/json")));
    }

    @Test
    public void testGetPostById() {
        var id = UUID.randomUUID();
        var data = new Post(id, "title1", "content1", Status.DRAFT, LocalDateTime.now());

        stubFor(get("/posts/" + id)
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withResponseBody(Body.fromJsonBytes(Json.toByteArray(data)))
                )
        );

        var post = postClient.getById(id);
        assertThat(post.id()).isEqualTo(id);
        assertThat(post.title()).isEqualTo(data.title());
        assertThat(post.content()).isEqualTo(data.content());
        assertThat(post.status()).isEqualTo(data.status());
        assertThat(post.createdAt()).isEqualTo(data.createdAt());


        verify(getRequestedFor(urlEqualTo("/posts/" + id))
                .withHeader("Accept", equalTo("application/json"))
        );
    }

    @Test
    public void testGetPostById_NotFound() {
        var id = UUID.randomUUID();
        stubFor(get("/posts/" + id)
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withStatus(404)
                                .withResponseBody(Body.none())
                )
        );

        assertThatThrownBy(() -> postClient.getById(id)).isInstanceOf(PostNotFoundException.class);

        verify(getRequestedFor(urlEqualTo("/posts/" + id))
                .withHeader("Accept", equalTo("application/json"))
        );
    }

    @Test
    public void testCreatePost() {
        var id = UUID.randomUUID();
        var data = new Post(null, "title1", "content1", Status.DRAFT, null);

        stubFor(post("/posts")
                .willReturn(
                        aResponse()
                                .withHeader("Location", "/posts/" + id)
                                .withStatus(201)
                )
        );

        postClient.save(data);

        verify(postRequestedFor(urlEqualTo("/posts"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(Json.write(data)))
        );
    }

    @Test
    public void testUpdatePost() {
        var id = UUID.randomUUID();
        var data = new Post(null, "title1", "content1", Status.DRAFT, null);

        stubFor(put("/posts/" + id)
                .willReturn(
                        aResponse()
                                .withStatus(204)
                )
        );

        postClient.update(id, data);

        verify(putRequestedFor(urlEqualTo("/posts/" + id))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(Json.write(data)))
        );
    }

    @Test
    public void testDeletePostById() {
        var id = UUID.randomUUID();
        stubFor(delete("/posts/" + id)
                .willReturn(
                        aResponse()
                                .withStatus(204)
                )
        );

        postClient.delete(id);

        verify(deleteRequestedFor(urlEqualTo("/posts/" + id))
                .withHeader("Accept", equalTo("application/json"))
        );
    }
}
```
For a complete example, you can explore the [Spring RestClient example project](https://github.com/hantsy/spring6-sandbox/tree/master/rest-client) on GitHub. This project includes the source code and demonstrates how to use the `RestClient` in a real-world scenario.

