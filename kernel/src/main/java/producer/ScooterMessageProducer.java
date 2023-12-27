package producer;

import dto.ScooterDTO;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class ScooterMessageProducer {

    @Channel("scooter-unlock-requested")
    Emitter<ScooterDTO> unlockScooterChannel;

    @Channel("scooter-lock-requested")
    Emitter<ScooterDTO> lockScooterChannel;

    public Uni<Void> scooterUnlockRequested(ScooterDTO scooterDTO) {
        return Uni.createFrom().voidItem().invoke(() -> unlockScooterChannel.send(scooterDTO));
    }

    public Uni<Void> scooterLockRequested(ScooterDTO scooterDTO) {
        return Uni.createFrom().voidItem().invoke(() -> lockScooterChannel.send(scooterDTO));
    }

}
