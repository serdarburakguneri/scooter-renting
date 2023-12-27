package service;

import adapter.RentalHistoryAdapter;
import dto.RentRequestDTO;
import entity.RentalHistory;
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

    public Uni<RentalHistory> requestRenting(RentRequestDTO request) {
        return scooterService.validateUnlocking(request.scooterId())
                .replaceWith(repository.persist(RentalHistoryAdapter.fromRentRequestDTO(request)))
                .call(() -> scooterService.requestUnlock(request.scooterId()));
    }

}
