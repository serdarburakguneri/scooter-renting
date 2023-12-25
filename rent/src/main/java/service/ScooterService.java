package service;

import adapter.ScooterAdapter;
import adapter.ScooterDTOAdapter;
import dto.ScooterCreationDTO;
import dto.ScooterDTO;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import java.util.List;
import java.util.stream.Collectors;
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
                .failWith(new BadRequestException("A scooter with provided serialNumber exists!"))
                .replaceWith(ScooterAdapter.fromScooterCreationDTO(request))
                .call(scooterRepository::persist)
                .map(ScooterDTOAdapter::fromScooter);
    }

    //TODO: filtering is a good idea
    public Uni<List<ScooterDTO>> list() {
        return scooterRepository
                .findAll()
                .list()
                .onItem()
                .transform(
                        scooters -> scooters.stream()
                                .map(ScooterDTOAdapter::fromScooter)
                                .collect(Collectors.toList()));

    }
}
