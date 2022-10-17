package com.example.demo.client;

import com.example.demo.domain.model.Post;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.service.RSocketExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface PostClientService {

    @RSocketExchange("posts.findAll")
    public Flux<Post> all();

    @RSocketExchange("posts.titleContains")
    public Flux<Post> titleContains(@Payload String title);

    @RSocketExchange("posts.findById.{id}")
    public Mono<Post> get(@DestinationVariable("id") UUID id);

    @RSocketExchange("posts.save")
    public Mono<UUID> create(@Payload Post post);

    @RSocketExchange("posts.update.{id}")
    public Mono<Boolean> update(@DestinationVariable("id") UUID id, @Payload Post post);

    @RSocketExchange("posts.deleteById.{id}")
    public Mono<Boolean> delete(@DestinationVariable("id") UUID id);

}