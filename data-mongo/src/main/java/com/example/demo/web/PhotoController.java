package com.example.demo.web;

import com.mongodb.MongoGridFSException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/photos")
@RequiredArgsConstructor
@Slf4j
public class PhotoController {
    private final GridFsTemplate template;

    @SneakyThrows
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestPart MultipartFile file) {
        var metadata = Map.of("author", "hantsy");
        var id = template.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), metadata).toHexString();
        log.debug("upload file id: {}", id);

        return ok(Map.of("id", id));
    }

    @SneakyThrows
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> download(@PathVariable String id) {
        var fsFile = template.findOne(query(where("_id").is(new ObjectId(id))));
        if (fsFile == null) {
            return notFound().build();
        }
        var resource = template.getResource(fsFile);

        // set headers
        var headers = new HttpHeaders();
        headers.setContentLength(resource.contentLength());
        headers.setContentType(MediaType.valueOf(resource.getContentType()));

        // set response
        return new ResponseEntity<>(new InputStreamResource(resource.getContent()), headers, HttpStatus.OK);
    }

    @SneakyThrows
    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            template.delete(query(where("_id").is(new ObjectId(id))));
            return noContent().build();
        } catch (MongoGridFSException e) {
            log.debug("exception caught when deleting file: {}", e.getMessage());
            return notFound().build();
        }
    }
}

