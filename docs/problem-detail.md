# An Introduction to Spring ProblemDetails Support

When building REST API backends, developers often create custom wrappers, such as `ApiResult` or `ErrorResponse` to standardize response formats within their projects. However, these solutions are rarely portable across different systems. As a developer, I find it frustrating to handle various response formats when integrating with third-party APIs.

[Spring HATEOAS](https://spring.io/projects/spring-hateoas) adopts the [VndError draft proposal](https://github.com/blongden/vnd.error) to represent REST response messages. While Spring HATEOAS primarily focuses on building hypermedia-driven APIs, it also helps applications reach Level 3 of the Richardson Maturity Model.

Another widely accepted format is Problem Details, standardized by the IETF as [RFC9457](https://www.rfc-editor.org/rfc/rfc9457.html). Problem Details for HTTP APIs defines a consistent, human being friendly and readable structure for representing error conditions in HTTP responses. This specification enables clients to interpret and handle errors uniformly, simplifying integration and improving interoperability across different systems.

Finally Spring 6 adds native support for ProblemDetails, making it easier for developers to adopt this consistent error format in their applications.

Let’s take a closer look at the new [`ProblemDetail`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ProblemDetail.html) class introduced in Spring 6. This POJO includes several fields defined by RFC 9457:

* `type` – A URI identifying the problem type
* `status` – The HTTP status code
* `title` – A brief, human-readable summary of the problem
* `detail` – A comprehensive description of the problem
* `instance` – A URI reference that identifies the specific occurrence of the problem, usually the REST path
* `properties` – An extension point for adding custom fields

The `ProblemDetail` class provides two convenient factory methods: `forStatus(HttpStatus status)` and `forStatusAndDetail(HttpStatusCode status, String detail)`, making it easy to create ProblemDetail objects.

In a Spring WebMvc or WebFlux project, you can assemble error responses using `@ExceptionHandler` methods in a `@ControllerAdvice` or `@RestControllerAdvice` bean. These methods can return either a `ResponseEntity<ProblemDetail>` or a `ProblemDetail` directly:

```java
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFoundException(PostNotFoundException exception) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problemDetail.setProperty("id", exception.getPostId());
        problemDetail.setProperty("entity", "POST");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
}
```

When an exception is handled, the response is rendered with the content type `application/problem+json`, and the body follows this structure:

```json
{
    "type": "http://example.com/apidoc/error/404",
    "status": 404,
    "title": "Post Not Found",
    "detail": "Post is not found, the id: xxxx",
    "instance": "/api/posts/xxxx"
}
```

When using the Jackson library, custom properties added to the `properties` map are serialized as top-level JSON fields, thanks to the `ProblemDetailJacksonMixin`. For example, if you set custom properties via `setProperty("errors", obj)`:

```java
detail.setProperty("errors",
    List.of(
        Map.of("path", "title", "detail", "Cannot be empty")
    )
);
```

The resulting response will include the custom `errors` field at the top level:

```json
{
    // ...
    "status": 400,
    "instance": "/api/posts/xxxx",
    "errors": [
        {
            "path": "title",
            "detail": "Cannot be empty"
        }
    ]
}
```

You can explore the complete [sample code](https://github.com/hantsy/spring6-sandbox/tree/master/problem-details) on GitHub.

To enable ProblemDetails support in a Spring Boot project, add the following properties to your *application.properties* file:

```properties
# For Spring WebMvc
spring.mvc.problemdetails.enabled=true

# For Spring WebFlux
spring.webflux.problemdetails.enabled=true
```

Once enabled, you can use ProblemDetails as demonstrated above. Additionally, Spring Boot’s built-in error handling will also return errors in the ProblemDetails format.

Before the introduction of native support in Spring 6, developers who wanted to adopt the Problem Details format often turned to [Zalando Problem](https://github.com/zalando/problem). This library, along with its [Spring Web integration](https://github.com/zalando/problem-spring-web), provides comprehensive support for both WebMvc and WebFlux applications.

> [!NOTE]
> Zalando’s Problem library and its Spring integration module offer more extensive integration with the Spring ecosystem than the current built-in support in Spring Boot. For instance, it can automatically handle security-related exceptions and Jakarta Validation errors, among other advanced features.

---

**Summary:**  
Spring 6 and Spring Boot now offer native support for the standardized ProblemDetails error format, making error handling more consistent and interoperable across applications. For advanced integration, especially in areas such as security and validation, Zalando's solution remains a strong alternative.
