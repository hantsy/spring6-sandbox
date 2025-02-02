# Spring Jdbc and Vibur DBCP

[Vibur DBCP](https://github.com/vibur/vibur-dbcp) is a fast, high-performance JDBC connection pool that provides SQL performance monitoring and logging capabilities.
The Vibur project includes two modules. The first provides a general-purpose object pool. Based on this feature, it implements a JDBC connection pool.

Vibur DBCP itself provides Hibernate and Spring Boot integration, but since Spring Boot 3.5, the built-in `DataSourceBuilder` has officially added support for Vibur DBCP.

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
> NOTE: You must specify the `initMethod` method to be executed when the class is instantiated, else the Vibur DBCP will not work as expected.

Set up the JDBC connection in the *application.properties*.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/testdb
spring.datasource.username=user
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver
```

In the `DataSource` bean declaration, when setting up the `type` to `ViburDBCPDataSource` class, it will bind the Jdbc connection properties, eg. `url`, `username`, `password`, and `driver-class-name` to a `ViburDBCPDataSource` instance through the methods of `ViburDBCPDataSource` that registered in the `DataSourceBuilder`.

Let's create a simple Entity to taste the Jdbc functionality.

```java
public record Product(Long id, String name, BigDecimal price) {}
```

Create a `schema.sql` and a `data.sql` in the *main/resources* folder to initialize the database at the application startup.

```sql
-- schemq.sql
CREATE TABLE IF NOT EXISTS products
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(200),
    price NUMERIC
);

-- data.sql
DELETE
FROM products;

INSERT INTO products(name, price)
VALUES ('Apple', 1.0);
```

Create a `ProductRepository` interface and define a method to fetch all `Product` records in the `products` table.

```java
public interface ProductRepository {
    List<Product> findAll();
}
```

Add an implementation class.

```java
@Repository
public class JdbcProductRepository implements ProductRepository {
    private final JdbcClient jdbcClient;

    public JdbcProductRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<Product> findAll() {
        String sql = """
                select * from products
                """;
        RowMapper<Product> rowMapper = (ResultSet rs, int rowNum) -> {
            var id = rs.getLong("id");
            var name = rs.getString("name");
            var price = rs.getBigDecimal("price");
            return new Product(id, name, price);
        };
        return jdbcClient.sql(sql)
                .query(rowMapper)
                .list();
    }
}
```

The `JdbcClient` bean is available when `Jdbc Starter` is added to the project dependencies.

Add a simple `CommandLineRunner` bean to print the data inserted in the database at the application startup.

```java
@SpringBootApplication
public class DemoApplication {
    // ...
    @Bean
    public CommandLineRunner commandLineRunner(ProductRepository productRepository) {
        return args -> productRepository.findAll().
                forEach(System.out::println);
    }
}
```

Create a *docker-compose.yml* file to serve a running Postgres database.

```yml
services:
  postgres:
    image: postgres
    ports:
      - "5432:5432"
    restart: always
    environment:
      POSTGRES_PASSWORD: password
      POSTGRES_DB: testdb
      POSTGRES_USER: user
    volumes:
      - ./data/postgresql:/var/lib/postgresql
      - ./pg-initdb.d:/docker-entrypoint-initdb.d
```

Click and run the `DemoApplication.main` method in IDE or execute `mvn spring-boot:run` in a terminal to build and run the application. You should see the product info in the console similar to the following.

```bash
Product[id=1, name=Apple, price=1.0]
```

Alternatively, create a simple test to verify the functionality of `ProductRepository`.

```java
@Autowired
ProductRepository productRepository;

@Test
void contextLoads() {
    List<Product> products = productRepository.findAll();
    products.forEach(System.out::println);

    assertThat(products).isNotEmpty();
}
```

At the moment I am writing this post, Spring Boot does not provide an `AutoConfiguration` class for Vibur DBCP as the existing Hiarku, Commons Dbcp, etc.
So no `spring.datasource.vibur` prefix-based properties are ready to configure Vibur DBCP via *application.properties*.

You can create a simple Vibur-specific Properties class and add the extra properties to configure the `ViburDBCPDataSource`.

Create a `record` class to hold all Vibur-specific properties.

```java
@ConfigurationProperties(prefix = "spring.datasource.vibur")
record ViburProperties(
        String name,
        int poolInitialSize,
        int poolMaxSize,
        int connectionTimeoutInMs,
        int loginTimeoutInSeconds,
        int logQueryExecutionLongerThanMs,
        int logConnectionLongerThanMs,
        boolean clearSQLWarnings
) {}
```

Change the above `DataSource` bean declaration to the following.

```java
@Bean(initMethod = "start", destroyMethod = "close")
DataSource virbDataSource(DataSourceProperties dataSourceProperties,
                          ViburProperties viburProperties) {
    log.debug("vibur properties: {}", viburProperties);
    var dataSource = DataSourceBuilder.create()
            .type(ViburDBCPDataSource.class)
            .url(dataSourceProperties.getUrl())
            .username(dataSourceProperties.getUsername())
            .password(dataSourceProperties.getPassword())
            .driverClassName(dataSourceProperties.getDriverClassName())
            .build();

    dataSource.setPoolInitialSize(viburProperties.poolInitialSize());
    dataSource.setPoolMaxSize(viburProperties.poolMaxSize());
    dataSource.setLoginTimeout(viburProperties.loginTimeoutInSeconds());
    dataSource.setConnectionTimeoutInMs(viburProperties.connectionTimeoutInMs());
    dataSource.setLogConnectionLongerThanMs(viburProperties.logConnectionLongerThanMs());
    dataSource.setLogQueryExecutionLongerThanMs(viburProperties.logQueryExecutionLongerThanMs());
    dataSource.setClearSQLWarnings(viburProperties.clearSQLWarnings());
    dataSource.setName(viburProperties.name());
    return dataSource;
}
```

Add a `@ConfigurationPropertiesScan` annotation to the `DemoApplication` class to activate the `ViburProperities`.

Now all the vibur-specific properties are bound to the prefix `spring.datasource.vibur`. You can customize them in the *application.properties*.

```properties
spring.datasource.vibur.pool-initial-size=2
spring.datasource.vibur.pool-max-size=10
spring.datasource.vibur.connection-timeout-in-ms=5000
spring.datasource.vibur.login-timeout-in-seconds=3
spring.datasource.vibur.log-query-execution-longer-than-ms=5
spring.datasource.vibur.log-connection-longer-than-ms=5
spring.datasource.vibur.clear-SQL-warnings=true
spring.datasource.vibur.name=viburPool
```

The above custom vibur properties feature is included in a [test class](https://github.com/hantsy/spring6-sandbox/blob/master/boot-vibur-dbcp/src/test/java/com/example/demo/ViburDatasourceTests.java).

Check out [the complete example project](https://github.com/hantsy/spring6-sandbox/tree/master/boot-vibur-dbcp) from my GitHub account.
