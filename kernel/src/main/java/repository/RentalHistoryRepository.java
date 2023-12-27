package repository;

import entity.RentalHistory;
import entity.Scooter;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Table;
import java.util.UUID;

@ApplicationScoped
@Table(name = "rental_history")
public class RentalHistoryRepository implements PanacheRepositoryBase<RentalHistory, UUID> {

}