package consumer;

import dto.ScooterDTO;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.resteasy.reactive.common.NotImplementedYet;

@ApplicationScoped
public class ScooterMessageConsumer {

    @Incoming("scooter-unlocked")
    public void consumeUnlocked(JsonObject message) {
        var scooter = message.mapTo(ScooterDTO.class);
        throw new NotImplementedYet();
    }

    @Incoming("scooter-available")
    public void consumeAvailable(JsonObject message) {
        var scooter = message.mapTo(ScooterDTO.class);
        throw new NotImplementedYet();
    }

}
