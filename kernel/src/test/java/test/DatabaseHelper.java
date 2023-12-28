package test;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import repository.RentalHistoryRepository;
import repository.ScooterRepository;

@ApplicationScoped
public class DatabaseHelper {

    private final ScooterRepository scooterRepository;
    private final RentalHistoryRepository rentalHistoryRepository;

    @Inject
    public DatabaseHelper(ScooterRepository scooterRepository, RentalHistoryRepository rentalHistoryRepository) {
        this.scooterRepository = scooterRepository;
        this.rentalHistoryRepository = rentalHistoryRepository;
    }

    public Uni<Void> cleanDatabase() {
        return rentalHistoryRepository.deleteAll()
                .replaceWith(scooterRepository.deleteAll())
                .replaceWithVoid();
    }
}
