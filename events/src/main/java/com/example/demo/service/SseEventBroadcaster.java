package com.example.demo.service;

import com.example.demo.event.transactional.PostCreatedEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event;

@Component
@Slf4j
public class SseEventBroadcaster {
    private final CopyOnWriteArraySet<SseEmitter> emitters = new CopyOnWriteArraySet<>();

    public void connect(SseEmitter emitter) {
        log.debug("connecting emitter: {}", emitter.toString());
        this.emitters.add(emitter);
        emitter.onCompletion(() -> {
            log.debug("completed");
            emitters.remove(emitter);
        });
        emitter.onError(error -> log.error(error.getMessage()));
        emitter.onTimeout(() -> {
            log.debug("timeout...");
            emitters.remove(emitter);
        });
    }

    @SneakyThrows
    @EventListener
    public void emitPostCreatedEvent(PostCreatedEvent event) {
        log.debug("emitting event: {}", event);
        this.emitters.forEach(emitter -> {
            try {
                var data = event()
                        .id(UUID.randomUUID().toString())
                        .data(event, MediaType.APPLICATION_JSON);
                emitter.send(data);
            } catch (IOException e) {
                log.error("emitting event failed with error: {}", e.getMessage());
                emitter.completeWithError(e);
            }
        });
    }
}
