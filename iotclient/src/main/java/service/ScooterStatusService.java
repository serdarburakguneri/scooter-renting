package service;

import entity.ScooterStatus;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ScooterStatusService {

    //I am going for a simple solution for now.
    private ScooterStatus status = ScooterStatus.AVAILABLE;

    public Uni<ScooterStatus> getStatus() {
        return Uni.createFrom().item(status);
    }

    public Uni<Void> updateStatus(ScooterStatus status) {
        this.status = status;
        return Uni.createFrom().voidItem();
    }
}
