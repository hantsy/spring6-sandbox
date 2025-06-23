# An Introduction to Spring ProblemDetails Support
When building REST API backend applications, developers often create custom wrappers like `ApiResult` or `ErrorResponse` to standardize response formats within their projects. However, these solutions are not portable across different systems. As a developer, I find it frustrating to handle various response formats when integrating with different third-party APIs.

[Spring HATEOAS](https://spring.io/projects/spring-hateoas) adopts the [VndError draft proposal](https://github.com/blongden/vnd.error) to represent REST response messages. While Spring HATEOAS is mainly focused on building hypermedia-driven APIs, it also helps applications reach the Richardson Maturity Model Level 3.

Another widely accepted format is Problem Details, standardized by the IETF as [RFC9457](https://www.rfc-editor.org/rfc/rfc9457.html). Problem Details for HTTP APIs(aka Problem Details) defines a consistent, machine-readable structure for representing error conditions in HTTP responses. This specification enables clients to interpret and handle errors uniformly, simplifying integration and improving interoperability across different systems.

Building on these industry standards, Spring 6 has introduced native support for ProblemDetails, making it easier for developers to adopt this consistent error format in their applications.

Let's take a closer look at [`ProblemDetail`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ProblemDetail.html), which includes several fields defined by RFC9457:

* `type` – A URI identifying the problem type
* `status` – The HTTP status code
* `title` – A brief, human-readable summary of the problem
* `detail` – A comprehensive description of the problem
* `instance` – A URI reference that identifies the specific occurrence of the problem, usually the REST path
* `properties` – An extension point for adding custom fields

`ProblemDetail` provides two convenient factory methods: `forStatus(HttpStatus status)` and `forStatusAndDetail(HttpStatusCode status, String detail)`, making it easy to create ProblemDetail objects.

In a Spring WebMvc or WebFlux project, you can assemble error responses using `@ExceptionHandler` methods:

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
    "detail": "Post is not found, the id : xxxx",
    "instance": "/api/posts/xxxx"
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

Once enabled, you can use ProblemDetails as demonstrated above. Spring Boot's built-in error handling will also return errors in the ProblemDetails format.

Before Spring 6, if you wanted to use Problem Details in your projects, you could consider using [`zalando/problem`](https://github.com/zalando/problem), which provides out-of-the-box [Spring Web integration](https://github.com/zalando/problem-spring-web) for both WebMvc and WebFlux.

> [!NOTE]
> Zalando's Problem Spring integration offers deeper integration with Spring components than the current Spring Boot built-in support. It handles security exceptions and Jakarta Validation violations, among other features.

---

**Summary:**  
Spring 6 and Spring Boot now offer native support for the standardized ProblemDetails error format, making error handling more consistent and interoperable. For advanced integration, especially with security and validation, Zalando's solution remains a strong alternative.
