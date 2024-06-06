# Integrating Jakarta Data with Spring 

As a Java backend developer, you could know well about the `Repository` pattern and the `Repository` facilities that provided in Spring Data, Quarkus, Micronaut Data etc. But every framework has it own advantages and limitations.

[Jakarta Data](https://jakarta.ee/specifications/data/) is a new Jakarta EE specification which tries to create an universal interfaces to access relational database and none-relational database. Jakarta Data 1.0 will be part of the upcoming [Jakarta EE 11](https://jakarta.ee/specifications/platform/11/). Currently the popular Jakarta Persistence Providers, such as Hibernate and Eclipse Link have implemented this specification in the early stage (because Jakarta Data 1.0 is not released yet).

In this post, we will use the latest Hibernate and try to integrate Jakarta Data into a Spring application.

