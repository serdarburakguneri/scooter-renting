package service;

import adapter.ScooterDTOAdapter;
import dto.RentRequestDTO;
import entity.RentalHistory;
import entity.RentalStatus;
import entity.Scooter;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.UUID;
import producer.ScooterMessageProducer;
import repository.RentalHistoryRepository;

@ApplicationScoped
public class RentService {

    public static final long MIN_BATTERY_LEVEL_FOR_RIDE = 5L;

    private final ScooterService scooterService;
    private final RentalHistoryRepository repository;

    private final ScooterMessageProducer scooterMessageProducer;

    @Inject
    public RentService(ScooterService scooterService, RentalHistoryRepository repository,
            ScooterMessageProducer scooterMessageProducer) {
        this.scooterService = scooterService;
        this.repository = repository;
        this.scooterMessageProducer = scooterMessageProducer;
    }

    public Uni<RentalHistory> requestRenting(RentRequestDTO request, String userId) {

        var rentalHistory = new RentalHistory.Builder()
                .withScooterId(request.scooterId())
                .withUserId(userId)
                .withStatus(RentalStatus.REQUESTED.name())
                .build();

        return validateUnlocking(request.scooterId())
                .replaceWith(repository.persist(rentalHistory))
                .call(() -> requestUnlock(request.scooterId()));
    }

    private Uni<Scooter> validateUnlocking(UUID id) {
        return scooterService.findById(id)
                .onItem()
                .ifNull()
                .failWith(new NotFoundException("A scooter with provided id could not be found."))
                .onItem()
                .call(scooter -> {
                    if (!scooter.isAvailable()) {
                        return Uni.createFrom()
                                .failure(new RuntimeException("The scooter is not available for unlocking."));
                    }
                    return Uni.createFrom().item(scooter);
                })
                .call(scooter -> {
                    if (scooter.getBatteryLevel().longValue() < MIN_BATTERY_LEVEL_FOR_RIDE) {
                        return Uni.createFrom()
                                .failure(new RuntimeException(
                                        "The battery level of the scooter is not suitable for driving"));
                    }
                    return Uni.createFrom().item(scooter);
                });
    }

    private Uni<Scooter> requestUnlock(UUID id) {
        return scooterService.findById(id)
                .map(scooter -> {
                    scooter.requestUnLock();
                    return scooter;
                })
                .call(this::publishUnlockRequested)
                .call(scooterService::update);
    }

    private Uni<Scooter> publishUnlockRequested(Scooter scooter) {
        var scooterDTO = ScooterDTOAdapter.fromScooter(scooter);
        return scooterMessageProducer.scooterUnlockRequested(scooterDTO).replaceWith(scooter);
    }

}
