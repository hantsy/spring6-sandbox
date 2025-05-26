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
