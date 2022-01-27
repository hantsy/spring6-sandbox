package com.example.demo.domain.repository;

import java.util.List;
import java.util.Map;

public interface PostRepositoryCustom {

    List<Map<String, Object>> countByStatus();
}
