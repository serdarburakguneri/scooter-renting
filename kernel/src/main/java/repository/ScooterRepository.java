package repository;

import entity.Scooter;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Table;
import java.util.UUID;

@ApplicationScoped
@Table(name = "scooter")
public class ScooterRepository implements PanacheRepositoryBase<Scooter, UUID> {
    public Uni<Scooter> findBySerialNumber(String serialNumber) {
        return find("serialNumber", serialNumber).firstResult();
    }
}