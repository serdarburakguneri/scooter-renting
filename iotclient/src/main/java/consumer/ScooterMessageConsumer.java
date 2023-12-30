package consumer;

import dto.ScooterDTO;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.resteasy.reactive.common.NotImplementedYet;
import service.ScooterService;

@ApplicationScoped
public class ScooterMessageConsumer {

    private final ScooterService scooterService;

    @Inject
    public ScooterMessageConsumer(ScooterService scooterService) {
        this.scooterService = scooterService;
    }

    @Incoming("scooter-unlock-requested")
    public Uni<Void> consumeScooterUpdate(JsonObject message) {
        var scooter = message.mapTo(ScooterDTO.class);
        return Uni.createFrom().failure(NotImplementedYet::new);
    }

}
