package producer;


import dto.ScooterDTO;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;


@ApplicationScoped
public class ScooterMessageProducer {

    @Channel("scooter-updated")
    Emitter<ScooterDTO> scooterUpdateChannel;

    public Uni<Void> scooterUnlockRequested(ScooterDTO scooterDTO) {
        return Uni.createFrom().voidItem().invoke(() -> scooterUpdateChannel.send(scooterDTO));
    }

}
