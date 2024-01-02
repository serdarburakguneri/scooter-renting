package consumer;

import adapter.ScooterPatchDTOAdapter;
import dto.ScooterDTO;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import service.ScooterService;

@ApplicationScoped
public class ScooterMessageConsumer {

    private final ScooterService scooterService;

    @Inject
    public ScooterMessageConsumer(ScooterService scooterService) {
        this.scooterService = scooterService;
    }

    @Incoming("scooter-updated")
    @WithTransaction
    public Uni<Void> consumeScooterUpdate(JsonObject message) {
        var scooter = message.mapTo(ScooterDTO.class);
        var scooterPatchDTO = ScooterPatchDTOAdapter.fromScooterDTO(scooter);
        return scooterService.patchWithSerialNumber(scooter.serialNumber(), scooterPatchDTO).replaceWithVoid();
    }

}
