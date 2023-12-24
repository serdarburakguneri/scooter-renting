package service;

import adapter.ScooterAdapter;
import adapter.ScooterDTOAdapter;
import dto.ScooterCreationDTO;
import dto.ScooterDTO;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import repository.ScooterRepository;

@ApplicationScoped
public class ScooterService {

    private final ScooterRepository scooterRepository;

    @Inject
    public ScooterService(ScooterRepository scooterRepository) {
        this.scooterRepository = scooterRepository;
    }

    public Uni<ScooterDTO> create(ScooterCreationDTO request) {
        return scooterRepository
                .findBySerialNumber(request.serialNumber())
                .onItem()
                .ifNotNull()
                .failWith(new NotFoundException("A scooter with provided serialNumber exists!"))
                .replaceWith(ScooterAdapter.fromScooterCreationDTO(request))
                .call(scooterRepository::persist)
                .map(ScooterDTOAdapter::fromScooter);
    }
}
