# An introduction to Spring RestClient API

Spring Framework 6.1 introduces a new synchronous HttpClient - [`RestClient`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestClient.html), which is based on the existing [`RestTemplate`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html), but provides a collection of modern fluent APIs to send HTTP requests. 
> [!NOTE]
> If you have some experience with [`WebClient`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html) from the Spring WeFlux module, you can consider `RestClient` as the blocking version of `WebClient`.

`RestClient` has several static methods that can be used to create an instance quickly. 
* `RestClient.create()`
* `RestClient.baseUrl()` to accept a *baseUrl* 
* `RestClient.create(RestTemplate)` to reuse the existing `RestTemplate`

Alternatively, it provides a convenient `builder()` method that returns a `RestClient.Builder` that can be used to customize the properties, such as *baseUrl*, *HttpClientRequestFactory*, *HttpMessageConverters*, etc. when building the `RestClient` instance.

The following is an example using `RestClient.builder` to declare a `RestClient` bean in Spring configuration.

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

To demonstrate the usage of `RestClient`, assume a collection of RESTful APIs served at *http://localhost:8080*.  

* `GET /posts` - Get all posts
* `POST /posts` - Create a new post, return a 201 status, and set the new URI in the `Location` header
* `GET /posts/{id}` - Get post by id, if not found returns a 404 status
* `PUT /posts/{id}` - Update a post
* `DELETE /posts/{id}` - Delete a post by id

