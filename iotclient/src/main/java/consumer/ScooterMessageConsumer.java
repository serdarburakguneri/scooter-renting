package consumer;

import dto.ScooterDTO;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.resteasy.reactive.common.NotImplementedYet;
import service.CommunicationService;

@ApplicationScoped
public class ScooterMessageConsumer {

    private final CommunicationService communicationService;

    @Inject
    public ScooterMessageConsumer(CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    // Now the problem is how to receive device specific messages. :)

    @Incoming("scooter-unlock-requested")
    public Uni<Void> consumeScooterUpdate(JsonObject message) {
        var scooter = message.mapTo(ScooterDTO.class);
        return Uni.createFrom().failure(NotImplementedYet::new);
    }

}
