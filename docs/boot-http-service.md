# Utilizing HTTP Service interfaces to Create API Contracts between Web Servers and Clients

In the [Declarative HTTP Client](./docs/declarative-http-client.md), we discussed how to create an HTTP/REST client using simple Java interfaces and new annotations introduced in Spring 6. On the server side, if you implement the interfaces and register it as a Spring controller, it will recognize the annotations on the interface class and method level, and expose RESTful APIs that work well as the classic `@RestController` we created before. Thus the HTTP service interfaces can be worked as united API contracts shared between the servers and clients.

Let's refactor the example codes used in the [Declarative HTTP Client](./docs/declarative-http-client.md), and split it into 3 projects.

```bash
+ demo
|-- shared
|-- client
|-- server
\-- pom.xml
```

The pom.xml in the project root folder is a simple POM declarative, including 3 modules: `shared`, `client`, and `server`.

*  `shared` includes the HTTP service interfaces and data models
*  `client` is the client application that calls the server APIs.
*  `server` is the server side that exposes the RESTful APIs.

Let's have a look at the POM.xml in the project root folder.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.5.0</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<packaging>pom</packaging>
	<groupId>com.example.demo</groupId>
	<artifactId>demo-parent</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<description>Demo project for Spring Boot</description>
	<modules>
		<module>client</module>
		<module>shared</module>
		<module>server</module>
	</modules>
</project>
```

The `client` and `server` depends on `shared` module.  Let's explore the `shared`, `client` and `server` respectively.

##  Shared Module

The shared module just contains some simple POJO that works as the contract between servers and clients, and does not include any business-related implementations.

The `PostApi` is a pure Java interace, that contain the same codes we did in the [Declarative HTTP Client](./docs/declarative-http-client.md).

```java
@HttpExchange(url = "/posts")
public interface PostApi {
    @GetExchange(accept = MediaType.APPLICATION_JSON_VALUE)
    Flux<Post> allPosts();

    @GetExchange(value = "/{id}", accept = MediaType.APPLICATION_JSON_VALUE)
    Mono<Post> getById(@PathVariable("id") UUID id);

    @PostExchange(contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<Void>> save(@RequestBody Post post);

    @PutExchange(value = "/{id}", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<Void>> update(@PathVariable UUID id, @RequestBody Post post);

    @DeleteExchange(value = "/{id}")
    Mono<ResponseEntity<Void>> delete(@PathVariable UUID id);
}

```

The is no changes in `Post` and `Status` class, copy them into shared.

```java
public record Post(UUID id,
                   String title,
                   String content,
                   Status status,
                   LocalDateTime createdAt
) {
}

public enum Status {
    DRAFT,
    PENDING_MODERATION,
    PUBLISHED;
}
```

Create a `PostNotFoundExcpetion` which is raised when the post was not found by ID.

```java

public class PostNotFoundException extends RuntimeException {
    private final UUID id;

    public PostNotFoundException(UUID id) {
        super("Post: " + id + " not found");
        this.id = id;
    }

    public UUID id() {
        return id;
    }
}

```

## Server Module

Add the above the shared module as an dependency, also add `spring-boot-starter-webflux` to serve a WebServer based on the Reactor Netty.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.5.0</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.example.demo</groupId>
	<artifactId>server</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<properties>
		<java.version>21</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-webflux</artifactId>
		</dependency>

		<dependency>
			<groupId>com.example.demo</groupId>
			<artifactId>shared</artifactId>
			<version>${project.version}</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```

Implements `PostApi` defined in the shared module, and declares it as `@RestController`. No need to add route path matching rules, it will resolve the 
annotations in the `PostApi` interface.

```java
@RestController
public class PostApiController implements PostApi {
    private final PostRepository posts;

    public PostApiController(PostRepository posts) {
        this.posts = posts;
    }

    @Override
    public Flux<Post> allPosts() {
        return this.posts.findAll();
    }

    @Override
    public Mono<Post> getById(UUID id) {
        return this.posts.findById(id);
    }

    @Override
    public Mono<ResponseEntity<Void>> save(Post post) {
        return this.posts.save(post)
                .map(id -> ResponseEntity.created(URI.create("/posts/" + id)).build());
    }

    @Override
    public Mono<ResponseEntity<Void>> update(UUID id, Post post) {
        return this.posts.update(id, post)
                .then(Mono.fromCallable(() -> ResponseEntity.noContent().build()));
    }

    @Override
    public Mono<ResponseEntity<Void>> delete(UUID id) {
        return this.posts.deleteById(id)
                .then(Mono.fromCallable(() -> ResponseEntity.noContent().build()));
    }
}
```

The `PostRepository` behaves as a *Repository* pattern.  

```java
public interface PostRepository {
    Mono<Post> findById(UUID id);
    Flux<Post> findAll();
    Mono<UUID> save(Post post);
    Mono<Void> update(UUID id, Post post);
    Mono<Void> deleteById(UUID id);
}
```

And there we use a dummy implementation by `Map` instead of the real database.

```java
@Repository
public class InMemoryPostRepository implements PostRepository {

    private final Map<UUID, Post> posts = new ConcurrentHashMap<>();

    @Override
    public Flux<Post> findAll() {
        return Flux.fromIterable(posts.values());
    }

    @Override
    public Mono<Post> findById(UUID id) {
        if (posts.containsKey(id)) {
            return Mono.just(posts.get(id));
        }

        return Mono.error(new PostNotFoundException(id));
    }

    @Override
    public Mono<UUID> save(Post post) {
        UUID id = UUID.randomUUID();
        Post newPost = new Post(id, post.title(), post.content(), Status.DRAFT, LocalDateTime.now());
        posts.put(id, newPost);
        return Mono.just(id);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        if (posts.containsKey(id)) {
            posts.remove(id);
            return Mono.empty();
        }
        return Mono.error(new PostNotFoundException(id));
    }

    @Override
    public Mono<Void> update(UUID id, Post post) {
        if (posts.containsKey(id)) {
            Post updatedPost = new Post(id, post.title(), post.content(), post.status(), posts.get(id).createdAt());
            posts.put(id, updatedPost);
            return Mono.empty();
        }

        return Mono.error(new PostNotFoundException(id));
    }
}
```

Add a simple `SampleDataInitializer` to listen the `ApplicationReadyEvent` and insert some sample data at the application startup stage. 

```java
@Component
public class SampleDataInitializer {
    private static final Logger log = LoggerFactory.getLogger(SampleDataInitializer.class);

    private final PostRepository posts;

    public SampleDataInitializer(PostRepository posts) {
        this.posts = posts;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        Flux.just("First", "Second")
                .map(it -> new Post(null, it + " Post", "Content of " + it + " Post", Status.DRAFT, LocalDateTime.now()))
                .flatMap(posts::save)
                .subscribe(
                        data -> log.debug("saved post: {}", data),
                        error -> log.error("error: {}", error),
                        () -> log.info("saved post successfully")
                );
    }
}
```

To handle the `PostNotFoundException`, we added a `RestExceptionHandler` to archive it.

```java
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlePostNotFoundException(PostNotFoundException e) {
        var error = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        error.setType(URI.create("http://localhost:8080/errors/404"));
        error.setProperty("id", e.id());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
```

Here we tried to convert the exception to friendly error messages defined in [Problem Details for HTTP APIs ](https://datatracker.ietf.org/doc/html/rfc7807).

Now run the *Application* main method in IDE or run `mvn clean spring-boot:run` to build and run the application.

Let's try to access the endpoints by `cURL` command line.

```bash
curl -X GET http://localhost:8080/posts -H "Accept: application/json"
[{"id":"b387888f-abb9-47ae-9f52-c5691c1a151a","title":"Second Post","content":"Content of Second Post","status":"DRAFT","createdAt":"2025-05-26T10:07:22.0166966"},{"id":"2da20365-089f-4a4f-8335-86b7516e6e14","title":"First Post","content":"Content of First Post","status":"DRAFT","createdAt":"2025-05-26T10:07:22.0156964"}]

curl -X GET http://localhost:8080/posts/19578802-e666-4ea5-a351-ce753f4c14d7 -H "Accept: application/json"
{"type":"http://localhost:8080/errors/404","title":"Not Found","status":404,"detail":"Post: 19578802-e666-4ea5-a351-ce753f4c14d7 not found","instance":"/posts/19578802-e666-4ea5-a351-ce753f4c14d7","id":"19578802-e666-4ea5-a351-ce753f4c14d7"}

```

Let's build the *client* module.

## Client Module

In a Microservice architecture, the *server* module could be deployed as a standalone small service that serves the **Post APIs**,  and the *client* could be shared as an SDK in other services to call the posts APIs in the *Post service*. Ideally, the *client* module should be a non-web application and can be used as a library. 

The POM of the client project is like the following:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.0</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example.demo</groupId>
    <artifactId>client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <properties>
        <java.version>21</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>

        <dependency>
            <groupId>com.example.demo</groupId>
            <artifactId>shared</artifactId>
            <version>${project.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

And in the *application.properties*, we set the `spring.main.web-application-type=none` and it will not start a web server as the *server* module.

As described in the [Declarative HTTP Client](./docs/declarative-http-client.md), create a `PostApi` client using `HttpServiceProxyFactory` builder.

```java
@Configuration
public class ClientConfig {
    private static final Logger log = LoggerFactory.getLogger(ClientConfig.class);

    @Bean
    WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8080")
                .build();
    }

    @Bean
    PostApi postClient(WebClient webClient) {
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builder()
                        .exchangeAdapter(WebClientAdapter.create(webClient))
                        .conversionService(new DefaultFormattingConversionService())
                        .build();
        return httpServiceProxyFactory.createClient(PostApi.class);
    }
}
```

Similarly, we can listen to  `ContextRefreshedEvent` to interact with the Post APIs exposed in the *server* module.

```java
@Component
public class ClientExampleInitializer {
    private static final Logger log = LoggerFactory.getLogger(ClientExampleInitializer.class);
    private final PostApi client;

    public ClientExampleInitializer(PostApi client) {
        this.client = client;
    }

    @EventListener(ContextRefreshedEvent.class)
    void postClientExample() {

        log.debug("get all posts.");
        client.allPosts()
                .subscribe(
                        data -> log.debug("The existing post: {}", data)
                );

        log.debug("save post and update post");
        client.save(new Post(null, "test", "test content", Status.DRAFT, null))
                .log()
                .flatMap(saved -> {
                    var uri = saved.getHeaders().getLocation().toString();
                    var idString = uri.substring(uri.lastIndexOf("/") + 1);
                    log.debug("Post id: {}", idString);
                    return client.getById(UUID.fromString(idString))
                            .log()
                            .map(post -> {
                                log.debug("post: {}", post);
                                return post;
                            });
                })
                .flatMap(post -> {
                    log.debug("getting post: {}", post);
                    return client.update(post.id(), new Post(null, "updated test", "updated content", Status.PENDING_MODERATION, null));
                })
                .subscribe(
                        responseEntity -> log.debug("updated status: {}", responseEntity)
                );
    }
}
```

Make sure the *server* application is running, and start the *client* application. 

To handle the *Post Not Found* exception in the *client* application, add a new code snippet in the listener method. 

```java
log.debug("get post by id that not existed.");
client.getById(UUID.randomUUID())
.subscribe(
	post -> log.debug("post: {}", post),
	error -> log.error("error:", error)
);
```

And add a custom status handler in the `WebClient` bean like this.

```java
@Configuration
public class ClientConfig {
    private static final Logger log = LoggerFactory.getLogger(ClientConfig.class);

    @Bean
    WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8080")
		.defaultStatusHandler(HttpStatusCode::is4xxClientError,ClientResponse::createError)
                .build();
    }
}
```

The above handler will track the HTTP status, all 4xx status will throw a `WebClientResponseException` in the reactive flow. In a caller module, you can handle the exception in the `onError` callback like this.

```java
postService.getById(id).onError().subscrbe(...);
```

Or define a global exception, for example - `PostServiceException` and convert `WebClientResponseException` into this *client* module-friendly exception.

```java
.defaultStatusHandler(HttpStatusCode::is4xxClientError,
	response -> response.createException()
                .map(it -> new PostServiceException(it.getResponseBodyAsString()))
```

In the caller module, it can use a global exception handler to handle this exception.

Or restore the original exception defined in the *server* posts APIs, 

```java
.defaultStatusHandler(status -> status == HttpStatus.NOT_FOUND,
	response -> response.createException()
		.map(it -> {
		    ProblemDetail problemDetails = null;
		    try {
			problemDetails = objectMapper.readValue(it.getResponseBodyAsByteArray(), ProblemDetail.class);
		    } catch (IOException e) {
			throw new RuntimeException(e);
		    }
		    log.debug("extracting exception body to problem details: {}", problemDetails);

		    return new PostNotFoundException(UUID.fromString(problemDetails.getProperties().get("id").toString()));
		})
)
```
For every exception, you can apply different handling strategies. 

>[!WARNING]
>I found there is an issue when adding the status handler here, if we set the application type as none web, it will raise a `WebClientRequestException` instead.

Get the [complete example codes](https://github.com/hantsy/spring6-sandbox/tree/master/boot-http-service) from my Github, and explore it yourself. 
