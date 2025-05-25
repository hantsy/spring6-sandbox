# An Introduction to Spring Declarative RSocket Client


[RSocket](https://rsocket.io) is an application protocol for multiplexed, duplex communication over TCP, WebSocket, and other byte stream transports. 

Since Spring 5.x, Spring includes RSocket support based on the RSocket Java implementations. On the server side, the RSocket messages handling is based on the existing Spring Messaging infrastructure. 

Add the following dependencies into project.

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

You can simplly the define a compoennt to process the incoming requests like this.

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

In the above the codes, 
* **`MessageMapping`** defines the message path matching rules.
* **`DestinationVariable`** to resovle the path varibles in the path.
* **`Payload`** indicate it reads the incoming messages as type-safe class.

To make it work, define an `RSocketServer` bean to serve the server-side message handling.

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

The `RSocketStrategies` bean defines the message encoders and decoders, route matches, etc. 

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

Run the RSocket server using the `RSocketServer` bean.

```java
var rSocketServer = context.getBean(RSocketServer.class);
rSocketServer.bind(TcpServerTransport.create("localhost", 7000))
        .block();
```
  
If you are using Spring Boot, to run an RSocket application, just need to add `spring-boot-starter-rsocket` and set the transport protocol type and server port in the *application.properties*. 

```properties
spring.rsocket.server.port=7000
spring.rsocket.server.transport=tcp
```

> [!NOTE]
> Unlike the general web application, it does not need to serve an HTTP/Web server when using TCP as the transport protocol.

Run the application, now the RSocket server is ready to accept client connections. Spring provides a simple `RSocketRequester` to simplify the client connection and sending requests.

Define an `RSocketRequester` bean.

```java
@Bean
RSocketRequester rSocketRequester(RSocketStrategies strategies) {
    return RSocketRequester.builder()
            .rsocketStrategies(strategies)
            .tcp("localhost", 7000);
}
```

You can simply using `RScoketRequester` to send a request like this:

```java

// get all posts
this.requester.route("posts.findAll").retrieveFlux(Post.class);

// get post by id
this.requester.route("posts.findById." + id).retrieveMono(Post.class);

// update post
this.requester.route("posts.update."+ id)
                .data(post)
                .retrieveMono(Post.class);

// delete post by id
return this.requester.route("posts.deleteById."+ id).send();
```

Since Spring 6, you can use a solution similar to [Declarative HTTP Client](./declarative-http-client.md) to define the operatins by a Java interface.

```java
public interface PostClient {

    @RSocketExchange("posts.findAll")
    public Flux<Post> all();

    @RSocketExchange("posts.titleContains")
    public Flux<Post> titleContains(@Payload String title);

    @RSocketExchange("posts.findById.{id}")
    public Mono<Post> get(@DestinationVariable("id") UUID id);

    @RSocketExchange("posts.save")
    public Mono<UUID> create(@Payload Post post);

    @RSocketExchange("posts.update.{id}")
    public Mono<Boolean> update(@DestinationVariable("id") UUID id, @Payload Post post);

    @RSocketExchange("posts.deleteById.{id}")
    public Mono<Boolean> delete(@DestinationVariable("id") UUID id);
}
```

And create a `PostClient` bean using `RSocketServiceProxyFactory`.

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

Then you can inject it in Spring components and use it like this.

```java
@Inject PostClient client;

this.client.all()
    .as(StepVerifier::create)
    .expectNextCount(2)
    .verifyComplete();
```

Get the complete example codes from my Github account, and explore it yourself.
