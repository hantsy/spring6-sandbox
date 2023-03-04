package com.example.demo.rest;

import com.example.demo.domain.PostNotFoundException;
import com.example.demo.domain.repository.PostRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Path("posts")
@RequestScope
public class PostResources {

    @Inject
    PostRepository posts;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") UUID id) {
        var data = posts.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        return Response.ok(data).build();
    }
}