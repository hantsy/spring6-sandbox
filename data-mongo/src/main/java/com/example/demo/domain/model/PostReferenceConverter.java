package com.example.demo.domain.model;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.mapping.DocumentPointer;

@WritingConverter
public class PostReferenceConverter implements Converter<Post, DocumentPointer<String>> {

    public DocumentPointer<String> convert(Post source) {
        return source::getSlug;
    }
}
