# An Introduction to Spring Declarative RSocket Client

[RSocket](https://rsocket.io) is an application protocol designed for multiplexed, duplex communication over transports like TCP and WebSocket. Since Spring 5.x, Spring has provided support for RSocket, building on the RSocket Java implementations. On the server side, RSocket message handling leverages Spring’s existing Messaging infrastructure.

## Getting Started: Adding Dependencies

To get started, add the following dependencies to your project:

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-messaging</artifactId>
</dependency>
<dependency>
    <groupId>io.rsocket</groupId>
    <artifactId>rsocket-core</artifactId>
</dependency>
<dependency>
    <groupId>io.rsocket</groupId>
    <artifactId>rsocket-transport-netty</artifactId>
</dependency>
```

## Defining an RSocket Controller

You can easily define a component to process incoming requests. Here’s a simple example:

```java
@Controller
@RequiredArgsConstructor
class PostController {

    private final PostRepository posts;

    @MessageMapping("posts.findAll")
    public Flux<Post> all() {
        return this.posts.findAll();
    }

    @MessageMapping("posts.titleContains")
    public Flux<Post> titleContains(@Payload String title) {
        return this.posts.findByTitleContains(title);
    }

    @MessageMapping("posts.findById.{id}")
    public Mono<Post> get(@DestinationVariable("id") UUID id) {
        return this.posts.findById(id);
    }

    @MessageMapping("posts.save")
    public Mono<UUID> create(@Payload Post post) {
        return this.posts.save(post);
    }

    @MessageMapping("posts.update.{id}")
    public Mono<Boolean> update(@DestinationVariable("id") UUID id, @Payload Post post) {
        return this.posts.findById(id)
                .map(p -> new Post(p.id(), post.title(), post.content(), post.status(), p.createdAt()))
                .flatMap(this.posts::update)
                .map(updated -> updated > 0);
    }

    @MessageMapping("posts.deleteById.{id}")
    public Mono<Boolean> delete(@DestinationVariable("id") UUID id) {
        return this.posts.deleteById(id)
                .map(deleted -> deleted > 0);
    }

}
```

**What’s happening above?**
- `@MessageMapping` specifies the RSocket route for each handler.
- `@DestinationVariable` extracts path variables from the route.
- `@Payload` binds the incoming message to a type-safe Java object.

## Setting Up the RSocket Server

To enable server-side RSocket message handling, define an `RSocketServer` bean:

```java
@Configuration
class ServerConfig {

    @Bean
    RSocketServer rSocketServer(RSocketMessageHandler handler) {
        return RSocketServer.create(handler.responder());
    }

    @Bean
    public RSocketMessageHandler rsocketMessageHandler(RSocketStrategies rsocketStrategies) {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.setRSocketStrategies(rsocketStrategies);
        return handler;
    }
}
```

The `RSocketStrategies` bean configures message encoders, decoders, and route matching strategies, etc.

```java
@Bean
public RSocketStrategies rsocketStrategies() {
    return RSocketStrategies.builder()
            .encoders(encoders -> encoders.add(new Jackson2CborEncoder()))
            .decoders(decoders -> decoders.add(new Jackson2CborDecoder()))
            .routeMatcher(new PathPatternRouteMatcher())
            .build();
}
```

The following is an example of retrieving `RSocketServer` bean from the Spring `ApplicationContext` and starting the RSocket server:

```java
var rSocketServer = context.getBean(RSocketServer.class);
rSocketServer.bind(TcpServerTransport.create("localhost", 7000))
        .block();
```

If you’re using Spring Boot, running an RSocket server is even easier! Just add the `spring-boot-starter-rsocket` dependency and configure the transport protocol and port in your `application.properties`:

```properties
spring.rsocket.server.port=7000
spring.rsocket.server.transport=tcp
```

> [!NOTE]  
> Unlike a typical web application, you don’t need to run an HTTP or Web server when using TCP as the RSocket transport.

Now, your RSocket server is ready to accept client connections.

## Connecting with RSocketRequester

Spring provides the `RSocketRequester` to streamline client connections and messaging. 

Define an `RSocketRequester` bean as follows:

```java
@Bean
RSocketRequester rSocketRequester(RSocketStrategies strategies) {
    return RSocketRequester.builder()
            .rsocketStrategies(strategies)
            .tcp("localhost", 7000);
}
```

You can now use `RSocketRequester` to send requests, for example:

```java
// Get all posts
this.requester.route("posts.findAll").retrieveFlux(Post.class);

// Get post by ID
this.requester.route("posts.findById." + id).retrieveMono(Post.class);

// Update post
this.requester.route("posts.update." + id)
                .data(post)
                .retrieveMono(Post.class);

// Delete post by ID
return this.requester.route("posts.deleteById." + id).send();
```

## Declarative RSocket Client (Spring 6+)

Similar to the solution described in [Declarative HTTP Client](./declarative-http-client.md), starting with Spring 6, you can define RSocket operations declaratively using a simple Java interface:

```java
public interface PostClient {

    @RSocketExchange("posts.findAll")
    Flux<Post> all();

    @RSocketExchange("posts.titleContains")
    Flux<Post> titleContains(@Payload String title);

    @RSocketExchange("posts.findById.{id}")
    Mono<Post> get(@DestinationVariable("id") UUID id);

    @RSocketExchange("posts.save")
    Mono<UUID> create(@Payload Post post);

    @RSocketExchange("posts.update.{id}")
    Mono<Boolean> update(@DestinationVariable("id") UUID id, @Payload Post post);

    @RSocketExchange("posts.deleteById.{id}")
    Mono<Boolean> delete(@DestinationVariable("id") UUID id);
}
```

Create a `PostClient` bean using the `RSocketServiceProxyFactory` builder tools:

```java
@Bean
public PostClient postClientService(RSocketRequester requester) {
    RSocketServiceProxyFactory rSocketServiceProxyFactory =
            RSocketServiceProxyFactory.builder()
                    .rsocketRequester(requester)
                    .blockTimeout(Duration.ofMillis(5000))
                    .build();
    return rSocketServiceProxyFactory.createClient(PostClient.class);
}
```

You can now inject and use your `PostClient` in any Spring component:

```java
@Inject PostClient client;

this.client.all()
    .as(StepVerifier::create)
    .expectNextCount(2)
    .verifyComplete();
```

---

For the complete example, clone [the source codes from my GitHub]() and explore the code yourself!
