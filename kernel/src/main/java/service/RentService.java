package service;

import dto.RentRequestDTO;
import entity.RentalHistory;
import entity.RentalStatus;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import repository.RentalHistoryRepository;

@ApplicationScoped
public class RentService {

    private final ScooterService scooterService;
    private final RentalHistoryRepository repository;

    @Inject
    public RentService(ScooterService scooterService, RentalHistoryRepository repository) {
        this.scooterService = scooterService;
        this.repository = repository;
    }

    public Uni<RentalHistory> requestRenting(RentRequestDTO request, String userId) {

        var rentalHistory = new RentalHistory.Builder()
                .withScooterId(request.scooterId())
                .withUserId(userId)
                .withStatus(RentalStatus.REQUESTED.name())
                .build();

        return scooterService.validateUnlocking(request.scooterId())
                .replaceWith(repository.persist(rentalHistory))
                .call(() -> scooterService.requestUnlock(request.scooterId()));
    }

}
