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


    @Inject
    public ScooterService(ScooterRepository scooterRepository) {
        this.scooterRepository = scooterRepository;
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

    public Uni<Scooter> update(Scooter scooter) {
        return scooterRepository
                .findById(scooter.getId())
                .onItem()
                .ifNull()
                .failWith(new NotFoundException("A scooter with provided id not present"))
                .replaceWith(scooter)
                .call(scooterRepository::persist);
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
