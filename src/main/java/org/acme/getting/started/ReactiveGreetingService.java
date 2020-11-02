package org.acme.getting.started;

import java.time.Duration;

import javax.enterprise.context.ApplicationScoped;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class ReactiveGreetingService {
    
    public Uni<String> greeting(String name){
        return Uni.createFrom().item(name)
            .onItem().transform(n -> String.format("Hello %s", name));
    }

    public Multi<String> greeting(int count, String name){
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onItem().transform(n -> String.format("Hello %s - %d", name , n))
                .transform().byTakingFirstItems(count);
    }
}
