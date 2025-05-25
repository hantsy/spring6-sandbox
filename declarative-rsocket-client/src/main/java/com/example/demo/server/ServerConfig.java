package com.example.demo.server;

import io.rsocket.core.RSocketServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;

@Configuration
class ServerConfig {

    @Bean
    RSocketServer rSocketServer(RSocketMessageHandler handler) {
        return RSocketServer.create(handler.responder());
    }

    @Bean
    public RSocketMessageHandler rsocketMessageHandler(RSocketStrategies rsocketStrategies) {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.setRSocketStrategies(rsocketStrategies);
        return handler;
    }
}