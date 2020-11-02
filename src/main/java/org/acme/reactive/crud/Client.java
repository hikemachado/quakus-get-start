package org.acme.reactive.crud;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.stream.StreamSupport;

public class Client {
    
    public Long id;

    public String name;

    public String instagram;

    public String interest;

    public String whatsContact;
    
    public Client(){

    }

    public Client(Long id, String name, String instagram, String interest, String whatsContact){
        this.id = id;
        this.name = name;
        this.instagram = instagram;
        this.interest = interest;
        this.whatsContact = whatsContact;
    }

    public static Multi<Client> findAll(PgPool client){
        return client.query("SELECT id, name, instagram, interest, contact FROM clients ORDER BY name ASC").execute()
                //Create multi from set rows
                .onItem().transformToMulti(set -> Multi.createFrom().items(() -> StreamSupport.stream(set.spliterator(), false)))
                //For each row create fruit instance
                .onItem().transform(Client::from);
    }

    public static Uni<Client> findById(PgPool client, Long id){
        return client.preparedQuery("SELECT id, name, instagram, interest, contact FROM clients WHERE id = $1").execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    public Uni<Long> save(PgPool client){
        return client.preparedQuery("INSERT INTO clients (name, instagram, interest, contact) VALUES ($1, $2, $3, $4) RETURNING (id)")
                .execute(Tuple.of(name, instagram, interest, whatsContact))
                .onItem().transform(pgRowSet -> pgRowSet.iterator().next().getLong("id"));
    }

    public Uni<Boolean> update(PgPool client){
        return client.preparedQuery("UPDATE clients SET name = $1, instagram = $2, interest = $3, contact = $4 WHERE id = $5")
                .execute(Tuple.of(name,instagram, interest, whatsContact, id))
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> delete(PgPool client, Long id){
        return client.preparedQuery("DELETE FROM clients WHERE id = $1").execute(Tuple.of(id))
        .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    private static Client from(Row row) {
        return new Client(row.getLong("id"),
                    row.getString("name"),
                    row.getString("instagram"),
                    row.getString("interest"),
                    row.getString("contact"));
    }
}
