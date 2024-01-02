package service;

import adapter.ScooterAdapter;
import adapter.ScooterDTOAdapter;
import dto.ScooterCreationDTO;
import dto.ScooterPatchDTO;
import entity.Scooter;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.UUID;
import producer.ScooterMessageProducer;
import repository.ScooterRepository;

@ApplicationScoped
public class ScooterService {

    private final ScooterRepository scooterRepository;
    private final ScooterMessageProducer scooterMessageProducer;
    public static final long MIN_BATTERY_LEVEL_FOR_RIDE = 5L;

    @Inject
    public ScooterService(ScooterRepository scooterRepository,
            ScooterMessageProducer scooterMessageProducer) {
        this.scooterRepository = scooterRepository;
        this.scooterMessageProducer = scooterMessageProducer;
    }

    public Uni<Scooter> findById(UUID id) {
        return scooterRepository.findById(id);
    }

    public Uni<Scooter> create(ScooterCreationDTO request) {
        return scooterRepository
                .findBySerialNumber(request.serialNumber())
                .onItem()
                .ifNotNull()
                .failWith(new BadRequestException("A scooter with provided serialNumber exists!"))
                .replaceWith(ScooterAdapter.fromScooterCreationDTO(request))
                .call(scooterRepository::persist);
    }

    public Uni<Scooter> validateUnlocking(UUID id) {
        return findById(id)
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

    public Uni<Scooter> requestUnlock(UUID id) {
        return findById(id)
                .map(scooter -> {
                    scooter.requestUnLock();
                    return scooter;
                })
                .call(this::publishUnlockRequested)
                .call(scooterRepository::persist);
    }

    private Uni<Scooter> publishUnlockRequested(Scooter scooter) {
        var scooterDTO = ScooterDTOAdapter.fromScooter(scooter);
        return scooterMessageProducer.scooterUnlockRequested(scooterDTO).replaceWith(scooter);
    }

    public Uni<Scooter> patchWithId(UUID scooterId, ScooterPatchDTO request) {
        return scooterRepository
                .findById(scooterId)
                .onItem()
                .ifNull()
                .failWith(new NotFoundException("A scooter with provided id not present"))
                .onItem()
                .transform(scooter -> ScooterAdapter.fromScooterPatchDTO(request, scooter))
                .call(scooterRepository::persist);
    }

    public Uni<Scooter> patchWithSerialNumber(String serialNumber, ScooterPatchDTO request) {
        return scooterRepository
                .findBySerialNumber(serialNumber)
                .onItem()
                .ifNull()
                .failWith(new NotFoundException("A scooter with provided serial number is not present"))
                .onItem()
                .transform(scooter -> ScooterAdapter.fromScooterPatchDTO(request, scooter))
                .call(scooterRepository::persist);
    }

    //TODO: filtering is a good idea
    public Uni<List<Scooter>> list() {
        return scooterRepository
                .findAll()
                .list();
    }
}
