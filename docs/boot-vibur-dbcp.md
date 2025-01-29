# Spring Jdbc and Vibur DBCP

[Vibur DBCP](https://github.com/vibur/vibur-dbcp) is a fast, high-performance JDBC connection pool that provides SQL performance monitoring and logging capabilities.
The Vibur project includes two modules. The first provides a general-purpose object pool. Based on this feature, it implements a JDBC connection pool.

Vibur DBCP itself provides Hibernate and Spring Boot integration, but since Spring Boot 3.5, the built-in `DataSourceBuilder` has officially added Vibur DBCP support.

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
spring.datasource.password=passowrd
spring.datasource.driver-class-name=org.postgresql.Driverill
```
> NOTE: At the moment I am writing this post, Spring Boot does not provide an `AutoConfiguration` class for Vibur DBCP as the existing Hiarku, etc.
> So there is no `spring.datasource.vibur` prefix-based properties to configure Vibur DBCP.

Let's create a simple test to verify the `ViburDBCPDataSource` is used.

```java
@SpringBootTest
@ActiveProfiles("ds")
@Testcontainers
class ViburDatasourceTests {

    @Container
    static PostgreSQLContainer PG_CONTAINER = new PostgreSQLContainer("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void setupDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", PG_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.password", PG_CONTAINER::getPassword);
        registry.add("spring.datasource.username", PG_CONTAINER::getUsername);
    }

    @TestConfiguration
    static class TestConfig {

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
    }

    @Autowired
    DataSource dataSource;

    @Test
    void contextLoads() {
        assertThat(dataSource).isInstanceOf(ViburDBCPDataSource.class);
        ViburDBCPDataSource ds = (ViburDBCPDataSource) dataSource;
        assertThat(ds.getUsername()).isEqualTo("user");
    }

}
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

Create a simple test to verify the functionality of `ProductRepository`.

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

Check out [the example project](https://github.com/hantsy/spring6-sandbox/tree/master/boot-vibur-dbcp) from my GitHub account.
