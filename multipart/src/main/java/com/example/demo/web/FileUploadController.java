package com.example.demo.web;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.*;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@Slf4j
public class FileUploadController {

    @SneakyThrows
    @PostMapping("form")
    public ResponseEntity handleFileUploadForm(FileUploadCommand form, BindingResult errors) {
        log.debug("uploading form data: {}", form);
        var result = Map.of(
                "name", form.getName(),
                "filename", form.getFile().filename()
        );
        return ok().body(result);
    }

    @PostMapping("requestparts")
    public ResponseEntity handleRequestParts(@RequestPart("name") String name, //Part name,
                                             @RequestPart("file") FilePart file) {
        log.debug("handling request parts: {}, {}", name, file);
        var result = Map.of(
                "name", name,
                "filename", file.filename()
        );
        return ok().body(result);
    }

//    @PostMapping("requestparts")
//    public ResponseEntity handleRequestParts(@Valid @RequestPart("meta-data") MetaData metadata) {
//    }

    @PostMapping("multivalues")
    public ResponseEntity handleMultivalues(@RequestBody Mono<MultiValueMap<String, Part>> parts) {
        log.debug("handling multivalues: {}", parts);
        var partNames = parts.map(p -> p.keySet().stream().map(key -> p.getFirst(key).name()).toList()).log();
        return ok().body(partNames);
    }

    @PostMapping("partevents")
    public ResponseEntity<Flux<String>> handlePartsEvents(@RequestBody Flux<PartEvent> allPartsEvents) {
        var result = allPartsEvents
                .windowUntil(PartEvent::isLast)
                .concatMap(p -> {
                            log.debug("contactMap boundary::");
                            return p.switchOnFirst((signal, partEvents) -> {
                                        if (signal.hasValue()) {
                                            PartEvent event = signal.get();
                                            if (event instanceof FormPartEvent formEvent) {
                                                String value = formEvent.value();
                                                // handle form field
                                                log.debug("form value: {}", value);
                                                return Mono.just(value + "\n");

                                            } else if (event instanceof FilePartEvent fileEvent) {
                                                String filename = fileEvent.filename();
                                                log.debug("upload file name:{}", filename);
//                                            Flux<DataBuffer> contents = partEvents.map(PartEvent::content);
//
//                                            // handle file upload
//                                            var fileBytes = DataBufferUtils.join(contents)
//                                                    .map(dataBuffer -> {
//                                                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
//                                                        dataBuffer.read(bytes);
//                                                        DataBufferUtils.release(dataBuffer);
//                                                        return bytes;
//                                                    });

                                                return partEvents.map(PartEvent::content)
                                                        .map(DataBufferUtils::release)
                                                        .then(Mono.just(filename + "\n"));
                                            }

                                            // no signal value
                                            return Mono.error(new RuntimeException("Unexpected event: " + event));

                                        }

                                        log.debug("return default flux");
                                        //return partEvents;
                                        return Flux.empty(); // either complete or error signal
                                    }
                            );
                        }
                );

        return ok().body(result);
    }

}
