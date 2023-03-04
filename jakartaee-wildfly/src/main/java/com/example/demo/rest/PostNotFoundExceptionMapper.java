package com.example.demo.rest;

import com.example.demo.domain.PostNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class PostNotFoundExceptionMapper implements ExceptionMapper<PostNotFoundException> {
    @Override
    public Response toResponse(PostNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
            .entity(Map.of("error", exception.getMessage()))
            .build();
    }
}
