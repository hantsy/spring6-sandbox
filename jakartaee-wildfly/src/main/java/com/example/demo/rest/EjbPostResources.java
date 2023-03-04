package com.example.demo.rest;

import com.example.demo.domain.ejb.PostService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("posts")
@Stateless
public class EjbPostResources {

    @EJB
    PostService posts;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") UUID id) {
        var data = posts.findById(id);
        return Response.ok(data).build();
    }
}
