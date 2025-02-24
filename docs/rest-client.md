# An introduction to Spring RestClient API

Spring framework 6.1 introduces a new synchorous HttpClient - [`RestClient`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestClient.html), which is based on the existing [`RestTemplate`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html), and provides a collection of modern and fluent APIs to send HTTP requests. 

> > NOTE:
> If you have some experience of [`WebClient`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html) from the Spring WeFlux module, you can consider `RestClient` as the blocking version of `WebClient`.

There are several convenient methods available in `RestClient` used to create a `RestClient` instance quickly. 
* `RestClient.create()`
* `RestClient.baseUrl()` to setup a *baseUrl* that to be connected 
* `RestClient.create(RestTemplate)` to reuse the existing `RestTemplate`

Alternatively, it provides another convenient `builder()` method  which returns a `RestClient.Builder` instance to customize the underlay properties, such as *baseUrl*, *HttpClientRequestFactory*, *HttpMessageConverters*, etc.

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

