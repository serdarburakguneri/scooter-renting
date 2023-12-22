package consumer;

import dto.RideDTO;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import service.EmailService;

@ApplicationScoped
public class RideConsumer {


    private final EmailService emailService;

    @Inject
    public RideConsumer(EmailService emailService) {
        this.emailService = emailService;
    }


    @Incoming("ride-started")
    public void consume(JsonObject message) {
        var user = message.mapTo(RideDTO.class);
        emailService.sendEmail(user.email(), "Hej", "You unlocked a scooter!");
    }

}
