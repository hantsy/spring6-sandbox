package com.example.demo.domain.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PostRepositoryImpl implements PostRepositoryCustom{

    @Autowired
    NamedParameterJdbcTemplate template;

    @Override
    public List<Map<String, Object>> countByStatus() {
        var sql = "SELECT count(*) as cnt, status FROM posts group by status";
        return this.template.queryForList(sql, Collections.emptyMap());
    }
}
