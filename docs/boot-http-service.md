# Utilizing HTTP Service Interfaces to Define API Contracts Between Web Servers and Clients

In the [Declarative HTTP Client](./docs/declarative-http-client.md), we explored how to create an HTTP/REST client using simple Java interfaces along with new annotations introduced in Spring 6. On the server side, when these interfaces are implemented and registered as Spring controllers, Spring automatically recognizes the annotations applied to the interface class and methods. This setup allows the server to expose RESTful APIs in the same way as traditional `@RestController` classes.

By leveraging HTTP service interfaces, we establish unified API contracts that can be shared between clients and servers, ensuring consistency, maintainability, and ease of development.

## Refactoring the Example Code

To better structure our application, we will refactor the example used in the Declarative HTTP Client and divide it into three separate projects:

```bash
+ demo
|-- shared
|-- client
|-- server
\-- pom.xml
```

The *pom.xml* file in the project’s root directory works as a simple BOM, organizing the following three modules:
- **shared**: Contains the HTTP service interfaces and data models, which will be used by both the client and server.
- **client**: Represents the client application that consumes the server’s APIs.
- **server**: Implements the server-side logic and exposes RESTful endpoints.

Let's examine the *pom.xml* file located in the root directory:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
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

Both the **client** and **server** modules depend on **shared**, ensuring the API contract is consistently enforced across the application.

## Exploring the Modules
Now, let's take a closer look at each module—**shared**, **client**, and **server**—to understand their roles and implementations.

###  Shared Module

The shared module serves as the foundation for communication between the client and server. It contains simple POJOs that define the API contract but does not include any business logic or implementation details.

#### Defining PostApi Interface

The `PostApi` is a pure Java interface that follows the same structure as the one introduced in the Declarative HTTP Client. It defines RESTful operations for managing posts, ensuring consistency between the client and server.

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

#### Post and Status Classes
No modifications are needed for the `Post` and `Status` classes. These classes define the structure of a post and its associated status. Simply copy them into the shared module.

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

#### Handling Post Not Found Errors
To manage cases where a requested post is not found, create a custom exception class called `PostNotFoundException`. This exception provides a meaningful error message when a post lookup fails.

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

### Server Module

To set up the server module, we add the *shared* module as a dependency and include `spring-boot-starter-webflux` to run a web server using Reactor Netty.

#### Add Shared Module as Dependency

Here’s the *pom.xml* configuration for the server module:

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

#### Implementing `PostApi`
 
We implement the `PostApi` interface from the shared module and declare it as a Spring `@RestController`. The routing annotations in the interface are automatically recognized, eliminating the need for explicit request mapping rules.

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

#### Implementing the PostRepository

The `PostRepository` follows the Repository pattern for managing posts.
 
```java
public interface PostRepository {
    Mono<Post> findById(UUID id);
    Flux<Post> findAll();
    Mono<UUID> save(Post post);
    Mono<Void> update(UUID id, Post post);
    Mono<Void> deleteById(UUID id);
}
```

Instead of integrating a real database, we use a `Map` as an in-memory store.

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

#### Initializing Sample Data at Startup

A simple initializer listens for the ApplicationReadyEvent and inserts sample data when the application starts.

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

#### Handling `PostNotFoundException`

To handle errors gracefully, we define a REST exception handler and return responses following [Problem Details for HTTP APIs ](https://datatracker.ietf.org/doc/html/rfc7807).

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

#### Running the Server Application

Start the application using your IDE or run:

```bash
mvn clean spring-boot:run
```

When the application is started successfully, you can use `cURL` to test the endpoints.

To fetch all posts:

```bash
curl -X GET http://localhost:8080/posts -H "Accept: application/json"
[{"id":"b387888f-abb9-47ae-9f52-c5691c1a151a","title":"Second Post","content":"Content of Second Post","status":"DRAFT","createdAt":"2025-05-26T10:07:22.0166966"},{"id":"2da20365-089f-4a4f-8335-86b7516e6e14","title":"First Post","content":"Content of First Post","status":"DRAFT","createdAt":"2025-05-26T10:07:22.0156964"}]
```

Try retrieving a non-existent post:

```bash
curl -X GET http://localhost:8080/posts/19578802-e666-4ea5-a351-ce753f4c14d7 -H "Accept: application/json"
{"type":"http://localhost:8080/errors/404","title":"Not Found","status":404,"detail":"Post: 19578802-e666-4ea5-a351-ce753f4c14d7 not found","instance":"/posts/19578802-e666-4ea5-a351-ce753f4c14d7","id":"19578802-e666-4ea5-a351-ce753f4c14d7"}
```

Now that we have the server module set up, let's move on to building the client module.

### Client Module

In a Microservices architecture, the **server** module operates as an independent service exposing **Post Service**, while the **client** module serves as an SDK that enables other services to interact with the **Post Service**. Ideally, the **client** module should be a `non-web` application designed to function as a reusable *library*.

#### Add Shared Module as Dependency

Here’s the *pom.xml* configuration for the client module

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

To ensure the client module does not start a web server (unlike the server module), configure *application.properties*.

```properties
spring.main.web-application-type=none
```

#### Creating a Declarative HTTP Client

Following the [Declarative HTTP Client](./docs/declarative-http-client.md), we configure a `PostApi` client using `HttpServiceProxyFactory`:

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
#### Interacting with the Post API

Using an `@EventListener`, we trigger interactions with the **server** module's APIs:

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

#### Handling Errors: Post Not Found Exception

To gracefully handle scenarios where a requested post does not exist, add this snippet to the listener method:
 
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

With this handler, all 4xx HTTP status codes will trigger a `WebClientResponseException` within the reactive flow.

In the caller module, exceptions can be handled in the `onError` callback:

```java
postService.getById(id).onError().subscrbe(...);
```

Alternatively, define a custom global exception like `PostServiceException`:

```java
.defaultStatusHandler(HttpStatusCode::is4xxClientError,
	response -> response.createException()
                .map(it -> new PostServiceException(it.getResponseBodyAsString()))
```

Or, restore the original exception from the server APIs:

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


>[!WARNING]
>I noticed an issue when adding the status handler: if the application type is set to non-web, it raises a `WebClientRequestException` instead.

Get the [complete example codes](https://github.com/hantsy/spring6-sandbox/tree/master/boot-http-service) from Github, and explore it yourself. 
