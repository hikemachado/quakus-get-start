package org.acme.reactive.crud;

import java.net.URI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;



@Path("clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {
    
    @Inject
    @ConfigProperty(name = "myapp.schema.clients.create", defaultValue = "true")
    boolean schemaCreate;

    @Inject
    PgPool dbClient;

    @PostConstruct
    void config(){
        if(schemaCreate){
            initdb();
        }
    }

    private void initdb(){
        dbClient.query("CREATE TABLE IF NOT EXISTS clients (id SERIAL PRIMARY KEY, name TEXT NOT NULL, instagram TEXT NOT NULL, interest TEXT NOT NULL, contact TEXT NULL)").execute()
                .await().indefinitely();

    }

    @GET
    public Multi<Client> get() {
        return Client.findAll(dbClient);
    }

    @GET
    @Path("{id}")
    public Uni<Response> getSingle(@PathParam Long id) {
        return Client.findById(dbClient, id)
                .onItem().transform(client -> client != null ? Response.ok(client) : Response.status(Status.NOT_FOUND))
                .onItem().transform(ResponseBuilder :: build);
    }

    @POST
    public Uni<Response> create(Client client) {
        return client.save(dbClient)
                .onItem().transform(id -> URI.create("/clients/" + id))
                .onItem().transform(uri -> Response.created(uri).build());
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(@PathParam Long id, Client client) {
        return client.update(dbClient)
                .onItem().transform(updated -> updated ? Status.OK : Status.NOT_FOUND)
                .onItem().transform(status -> Response.status(status).build());
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam Long id) {
        return Client.delete(dbClient, id)
        .onItem().transform(deleted -> deleted ? Status.NO_CONTENT : Status.NOT_FOUND)
        .onItem().transform(status -> Response.status(status).build());
    }

}
