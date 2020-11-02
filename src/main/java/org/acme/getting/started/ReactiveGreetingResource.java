package org.acme.getting.started;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.SseElementType;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@Path("/hello-reactive")
public class ReactiveGreetingResource {
    
    @Inject
    ReactiveGreetingService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/greeting/{name}")
    public Uni<String> greeting(@PathParam String name){
        return service.greeting(name);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/greeting/{count}/{name}")
    public Multi<String> greeting(@PathParam int count, @PathParam String name){
        return service.greeting(count, name);
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @SseElementType(MediaType.TEXT_PLAIN)
    @Path("/stream/{count}/{name}")
    public Multi<String> greetingAsStream(@PathParam int count, @PathParam String name){
        return service.greeting(count, name);
    }
}
