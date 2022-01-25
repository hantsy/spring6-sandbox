package com.example.demo.domain.model;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GenerateUUIDListener extends AbstractMongoEventListener<Album> {

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Album> event) {
        Album customer = event.getSource();
        if (customer.isNew()) {
            customer.setId(UUID.randomUUID());
        }
    }

}
