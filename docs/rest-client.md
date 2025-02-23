# An introduction to Spring RestClient API

Spring framework 6.1 introduces a new synchorous HttpClient - [`RestClient`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestClient.html), which is based on the existing [`RestTemplate`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html), but provides a collection of modern, fluent APIs to interact with RESTful service. 
> > NOTE:
> If you have some experience of [`WebClient`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html) from the Spring WeFlux module, you can consider `RestClient` as the blocking version of `WebClient`.

There are several static method in `RestClient` are used to create a `RestClient` instance quickly. 
* `RestClient.create()`
* `RestClient.baseUrl()` to accept a *baseUrl* 
* `RestClient.create(RestTemplate)` to reuse the existing `RestTemplate`

Alternatively, it provides a convenient `builder()` method to create `RestClient.Builder` and to customize properties, such as *baseUrl*, *HttpClientRequestFactory*, *HttpMessageConverters*, etc. when building the `RestClient` instance.

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

