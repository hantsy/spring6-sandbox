package com.example.demo;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
