# Spring Jdbc and Vibur DBCP

[Vibur DBCP](https://github.com/vibur/vibur-dbcp) is a fast, high-performance JDBC connection pool that provides SQL performance monitoring and logging capabilities.
The Vibur project includes two modules. The first provides a general-purpose object pool. Based on this feature, it implements a JDBC connection pool.

Vibur DBCP itself provides Hibernate and Spring Boot integration, but since Spring Boot 3.5, the built-in `DataSourceBuilder` has officially added Vibur DBCP support officially.

Generate a Spring Boot from [Spring Initialzer](http://start.spring.io), and set up the project as the following

* Spring Boot: `3.5.0-M1`
* Project: `Maven`
* Java: `21`
* Dependencies: `Web, JDBC API, Postgres, Testcontainers`

Keep other options as they were, download the project, and extract the files into your disk. Import the project into your IDE, eg. IntelliJ IDEA. 

Open *pom.xml* in the project root folder, and add the following dependencies.

```xml
<dependency>
  <groupId>org.vibur</groupId>
  <artifactId>vibur-dbcp</artifactId>
</dependency>
```

Then declare a `DataSource` bean in your configuration class.

```java
@Bean(initMethod = "start", destroyMethod = "close")
DataSource viburDataSource(DataSourceProperties dataSourceProperties) {
    return DataSourceBuilder.create()
            .type(ViburDBCPDataSource.class)
            .url(dataSourceProperties.getUrl())
            .username(dataSourceProperties.getUsername())
            .password(dataSourceProperties.getPassword())
            .driverClassName(dataSourceProperties.getDriverClassName())
            .build();
}
```
> NOTE: You must specify the `initMethod` method to be executed when the class is instantiated, else the Vibur DBCP does not work as expected.

Set up the JDBC connection in the *application.properties*.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/testdb
spring.datasource.username=user
spring.datasource.password=passowrd
spring.datasource.driver-class-name=org.postgresql.Driver
```
