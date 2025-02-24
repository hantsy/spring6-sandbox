# An introduction to Spring RestClient API

Spring Framework 6.1 introduces a new synchronous HttpClient - [`RestClient`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestClient.html), which is based on the existing [`RestTemplate`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html), but provides a collection of modern fluent APIs to send HTTP requests. 
> [!NOTE]
> If you have some experience with [`WebClient`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html) from the Spring WeFlux module, you can consider `RestClient` as the blocking version of `WebClient`.

`RestClient` provides several convenient methods to create an instance quickly. 

* `RestClient.create()`
* `RestClient.baseUrl()` to setup a *baseUrl* that to be connected 
* `RestClient.create(RestTemplate)` to reuse the existing `RestTemplate`

Alternatively, it also provides a convenient `builder()` method to get a `RestClient.Builder` that can be used to customize the properties, such as the default URI (via *baseUrl()*), the underlay HttpClient engine (via *requestFactory()*), and the message converters (via *messageConverters()*) used to encode/decode Http message body, etc. when building the `RestClient` instance.

The following is an example using `RestClient.Builder` to declare a `RestClient` bean in Spring `Configuration`.

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

`RestClient` can be used to interact with remote 3rd party HTTP APIs, and also can be used for lightweight service-to-service communication in a Microservice architecture.

To demonstrate the usage of `RestClient`, let's assume a collection of RESTful APIs served at *http://localhost:8080* that provide the following functionalities.  

* `GET /posts` - Get all posts
* `POST /posts` - Create a new post, return a 201 status, and set the new URI in the `Location` header
* `GET /posts/{id}` - Get post by id, if not found returns a 404 status
* `PUT /posts/{id}` - Update a post
* `DELETE /posts/{id}` - Delete a post by id

Create a `PostClient` bean to interact with the above APIs.

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

In the above codes, we use `RestClient` to shake hands with the APIs we defined formerly. 

Firstly `RestClinet` uses `method` or `get`/`post`/`put`/`delete` to set the HTTP method, then uses `uri`/`header`/`accept`/`contentType`, etc. to prepare HTTP request content, finally call `retrieve`/`exchange` to make the request. 
* The `retrieve` returns a `ResponseSpec` that is easier to extract the HTTP response body and headers or the entire HTTP entity.
* The `exchange` provides more options to control the raw HTTP request and response data.

If the target APIs are not accessible or not ready at the moment you are building the client codes, you can use [`MockRestServiceServer`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/client/MockRestServiceServer.html) or [`WireMock`](https://wiremock.org/) or [Spring Cloud Contract](https://spring.io/projects/spring-cloud-contract) to mock the remote APIs and verify the functionality of this client in an isolated environment.

The following is an example using `WireMock` to set up a mock environment that provides APIs in the testing codes.

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

    @SneakyThrows
    @BeforeEach
    public void setup() {
    }

    @SneakyThrows
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

    @SneakyThrows
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

    @SneakyThrows
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

    @SneakyThrows
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

    @SneakyThrows
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

Check out the [example project](https://github.com/hantsy/spring6-sandbox/tree/master/rest-client) from my GitHub account and explore the source codes yourself.

