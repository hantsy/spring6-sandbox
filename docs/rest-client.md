# An introduction to Spring RestClient API

Spring framework 6.1 introduces a new synchronous HttpClient - [`RestClient`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestClient.html), which is based on the existing [`RestTemplate`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html), but provides a collection of modern fluent APIs to send HTTP requests. 
> [!NOTE]
> If you have some experience with [`WebClient`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html) from the Spring WeFlux module, you can consider `RestClient` as the blocking version of `WebClient`.

`RestClient` has several static methods that can be used to quickly create an instance. 
* `RestClient.create()`
* `RestClient.baseUrl()` to accept a *baseUrl* 
* `RestClient.create(RestTemplate)` to reuse the existing `RestTemplate`

Alternatively, it provides a convenient `builder()` method which returns a `RestClient.Builder` and customizes the properties, such as *baseUrl*, *HttpClientRequestFactory*, *HttpMessageConverters*, etc. that are required to build the `RestClient` instance.

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

