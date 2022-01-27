package com.example.demo.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("users")
@Getter
@Setter
@ToString
public class User {

    @Id
    private UUID id;
    private String name;
    private String email;

    public static User of(String name, String email) {
        var user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
