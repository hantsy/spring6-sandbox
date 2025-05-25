package com.example.demo;

import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.springframework.context.annotation.*;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;

import java.io.IOException;

@Configuration
@ComponentScan
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
public class Application {
    public static void main(String[] args) throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                Application.class)) {
            var rSocketServer = context.getBean(RSocketServer.class);
            rSocketServer.bind(TcpServerTransport.create("localhost", 7000))
                    .doOnTerminate(() -> System.out.println("Server is shutting down"))
                    .blockOptional()
                    .ifPresentOrElse(
                            c -> {
                                System.out.println("Server is up on: " + c.address());
                                try {
                                 System.out.println("Enter q to exit");
                                 var exit = (char)System.in.read();
                                 if (exit == 'q') {
                                     System.out.println("Exiting...");
                                     System.exit(0);
                                 }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            },
                            () -> System.out.println("Closing...")
                    );
        }
    }

    @Bean
    RSocketServer rSocketServer(RSocketMessageHandler handler) {
        return RSocketServer.create(handler.responder());
    }

    @Bean
    public RSocketStrategies rsocketStrategies() {
        return RSocketStrategies.builder()
                .encoders(encoders -> encoders.add(new Jackson2CborEncoder()))
                .decoders(decoders -> decoders.add(new Jackson2CborDecoder()))
                .routeMatcher(new PathPatternRouteMatcher())
                .build();
    }
}
