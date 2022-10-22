package com.example.demo.web;

import com.example.demo.service.SseEventBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.Executor;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
public class SseEventController {

    private final SseEventBroadcaster sseEmitterHandler;
    private final Executor executor;

    @GetMapping(value = "", produces = TEXT_EVENT_STREAM_VALUE)
    public SseEmitter events() {
        var emitter = new SseEmitter();
        executor.execute(() -> this.sseEmitterHandler.connect(emitter));
        return emitter;
    }
}

