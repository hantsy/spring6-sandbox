package com.example.demo.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Table("post_labels")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class Label {

    @ToString.Include
    private String label;

    @Transient
    private Post post;

    public static Label of(String label) {
        var data = new Label();
        data.setLabel(label);
        return data;
    }
}
