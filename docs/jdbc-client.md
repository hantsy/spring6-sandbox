# An Introduction to Spring JdbcClient API

[`JdbcClient`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/simple/JdbcClient.html) is a fluent API that includes a collection 
of common Jdbc query and update operations, internally it delegates the execution to the existing [`JdbcTemplate`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html) 
and [`NamedParameterJdbcTemplate`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/namedparam/NamedParameterJdbcTemplate.html), and it supports both classic JDBC-style positional parameters and Spring-style named parameters in the SQL query string.

> [!NOTE]
> If you have used the [DatabaseClient](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/r2dbc/core/DatabaseClient.html) from the `spring-r2dbc` module, you should be impressed with the convenient methods provided in `DatabasClient`. In my opinion, the new `JdbcClient` in Spring 6.1 could be considered as a blocking variant of `DatabaseClient`.

`JdbcClient` is located in the `spring-jdbc` module, to use it in your project, just need to create a bean using the factory method `JdbcClient.create(...)`, which accepts a `DataSource`, `JdbcTemplate` or `NamedParameterJdbcTemplate` bean as the parameters. If you are using Spring Boot, add `spring-boot-starter-jdbc` or `spring-boot-starter-data-jdbc` in the project dependencies, and the `JdbcClient` bean is ready for you.

Let's create a Spring Boot project to demonstrate the usage of `JdbcClient`. Open your browser and navigate to [Spring Initializr](https://start.spring.io), generate a project using the following options.

* **Project**: Maven
* **Java**: 21
* **Dependencies**: JDBC API, Postgres, Testcontainers

Let's leave other options as they are.

Create a *schema.sql* and *data.sql* file to initialize the database at the application startup.

```sql
-- schema.sql
CREATE TABLE IF NOT EXISTS posts (
 id UUID DEFAULT uuid_generate_v4(),
 title VARCHAR(255),
 content VARCHAR(255),
 status VARCHAR(200) default 'DRAFT',
 created_at TIMESTAMP NOT NULL DEFAULT LOCALTIMESTAMP,
 PRIMARY KEY (id)
 );
-- data.sql
-- initialize the sample data.
DELETE FROM posts;
INSERT INTO posts (title, content) VALUES ('Spring 6 and Jdbc', 'Review the Jdbc features in Spring framework 6.0');
```

Create an interface to envelop the operations of the `posts` table. 

```java
public interface PostRepository {
    List<Post> findByTitleContains(String title);
    List<Post> findAll();
    Post findById(UUID id);
    UUID save(Post p);
    Integer update(Post p);
    Integer deleteById(UUID id);
    Integer deleteAll();
    Long count();
}
```

Add the following line in *application.properties* to make sure the scripts will be initialized at the application startup. 

```properties
spring.sql.init.mode=always
```

Let's have a look at the implementation class using the `JdbcClient` bean.

```java
@RequiredArgsConstructor
@Slf4j
@Repository
@Transactional
public class JdbcPostRepository implements PostRepository {

    public static final RowMapper<Post> ROW_MAPPER = (rs, rowNum) -> new Post(
            rs.getObject("id", UUID.class),
            rs.getString("title"),
            rs.getString("content"),
            //see: https://github.com/pgjdbc/pgjdbc/issues/2387
            //rs.getObject("status", Status.class),
            Status.valueOf(rs.getString("status")),
            rs.getObject("created_at", LocalDateTime.class)
    );

    private final JdbcClient client;

    @Override
    public List<Post> findByTitleContains(String name) {
        var sql = "SELECT * FROM posts WHERE title LIKE :title";
        return this.client.sql(sql)
                .params(Map.of("title", "%" + name + "%"))
                .query(ROW_MAPPER)
                .list();
    }

    @Override
    public List<Post> findAll() {
        var sql = "SELECT * FROM posts";
        return this.client.sql(sql).query(ROW_MAPPER).list();
    }

    @Override
    public Post findById(UUID id) {
        var sql = "SELECT * FROM posts WHERE id=:id";
        return this.client.sql(sql).params(Map.of("id", id)).query(ROW_MAPPER).single();
    }

    @Override
    public UUID save(Post p) {
        var sql = """
                INSERT INTO  posts (title, content, status) 
                VALUES (:title, :content, CAST(:status as post_status)) 
                RETURNING id
                """;
        var keyHolder = new GeneratedKeyHolder();
        var paramSource = new MapSqlParameterSource(
                Map.of("title", p.title(), "content", p.content(), "status", p.status().name())
        );
        var cnt = this.client.sql(sql).paramSource(paramSource).update(keyHolder);
        log.debug("updated count:{}", cnt);
        return keyHolder.getKeyAs(UUID.class);
    }

    @Override
    public Integer update(Post p) {
        var sql = "UPDATE posts set title=:title, content=:content, status=:status WHERE id=:id";
        Map<String, ? extends Serializable> params = Map.of(
                "title", p.title(),
                "content", p.content(),
                "status", p.status().name(),
                "id", p.id()

        );
        return this.client.sql(sql).params(params).update();
    }

    @Override
    public Integer deleteById(UUID id) {
        var sql = "DELETE FROM posts WHERE id=:id";
        return this.client.sql(sql).params(Map.of("id", id)).update();
    }

    @Override
    public Integer deleteAll() {
        var sql = "DELETE FROM posts";
        return this.client.sql(sql).update();
    }

    @Override
    public Long count() {
        var sql = "SELECT count(*) FROM posts";
        var count = this.client.sql(sql).query().singleValue();
        log.debug("count is: {}", count);
        return (Long)count;
    }
}
```

The above code snippets are easy to understand.
* The `JdbcClient.sql(...)` method accepts the SQL query string to execute, and returns a `StatementSpec`.
* The `StatementSpec.param` method and its variants bind the external parameters to the SQL statement by positions or names.
* The `StatementSpec.query` method is usually used to fetch and assemble the result from a `SELECT` statement, there are several variants.
   * The `StatementSpec.query()` returns a `ResultQuerySpec` which includes some methods on the raw `ResultSet` data.
   * The `StatementSpec.query(RowMapper)` returns a `MappedQuerySpec` which contains some methods on the converted type-safe data, the `RowMapper` parameter is used to convert Jdbc `ResultSet` to a type-safe class. According to the SQL execution results, calls `MappedQuerySpec.list()` and `MappedQuerySpec.single()` to return a `List` or a single type-safe object.
   * The usage of other variant methods, please refer to [JdbcClient Javadoc](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/simple/JdbcClient.html).
* Alternatively, you can use the `StatementSpec.update` method to perform mutations, generally it returns the affected rows count. Optionally it accepts `KeyHolder` to hold the newly inserted ID when executing an `INSERT` SQL statement.

Get [the complete example projects using Spring](https://github.com/hantsy/spring6-sandbox/tree/master/jdbc-client) and [Spring Boot](https://github.com/hantsy/spring6-sandbox/blob/master/boot-vibur-dbcp) from my GitHub account.  

   





