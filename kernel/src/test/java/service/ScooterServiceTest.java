package service;

import dto.ScooterCreationDTO;
import dto.ScooterPatchDTO;
import entity.Scooter;
import entity.ScooterStatus;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import repository.ScooterRepository;
import test.ScooterTestObject;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("UnitTest")
public class ScooterServiceTest {

    private ScooterService scooterService;
    private ScooterRepository scooterRepository;


    @BeforeEach
    void setUp() {
        scooterRepository = mock(ScooterRepository.class);
        scooterService = new ScooterService(scooterRepository);
    }

    @Test
    @DisplayName("It should be able to register a scooter")
    void create() {
        var request = new ScooterCreationDTO("12345ABCDE", "Bird", "One");

        var scooter = ScooterTestObject.get();

        when(scooterRepository.persist(any(Scooter.class)))
                .thenReturn(Uni.createFrom().item(scooter));

        var subscriber = scooterService.create(request)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();

        var scooterArgumentCaptor = ArgumentCaptor.forClass(Scooter.class);
        verify(scooterRepository, times(1)).persist(scooterArgumentCaptor.capture());
        assertEquals(scooterArgumentCaptor.getValue().getSerialNumber(), request.serialNumber());
        assertEquals(scooterArgumentCaptor.getValue().getBrand(), request.brand());
        assertEquals(scooterArgumentCaptor.getValue().getModel(), request.model());
    }

    @Test
    @DisplayName("It should be able to update a scooter")
    void update() {
        var scooter = ScooterTestObject.get();

        when(scooterRepository.findById(scooter.getId()))
                .thenReturn(Uni.createFrom().item(scooter));

        when(scooterRepository.persist(any(Scooter.class)))
                .thenReturn(Uni.createFrom().item(scooter));

        var subscriber = scooterService.update(scooter)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();

        var scooterArgumentCaptor = ArgumentCaptor.forClass(Scooter.class);
        verify(scooterRepository, times(1)).persist(scooterArgumentCaptor.capture());
        assertEquals(scooterArgumentCaptor.getValue().getSerialNumber(), scooter.getSerialNumber());
        assertEquals(scooterArgumentCaptor.getValue().getBrand(), scooter.getBrand());
        assertEquals(scooterArgumentCaptor.getValue().getModel(), scooter.getModel());
    }


    //TODO: make these two tests parametrized
    @Test
    @DisplayName("It should be able to patch a scooter with id")
    void patchWithId() {
        var scooter = ScooterTestObject.get();

        var request = new ScooterPatchDTO(BigDecimal.TEN, null, ScooterStatus.IN_USE.name());

        when(scooterRepository.findById(scooter.getId()))
                .thenReturn(Uni.createFrom().item(scooter));

        var subscriber = scooterService.patchWithId(scooter.getId(), request)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();

        var scooterArgumentCaptor = ArgumentCaptor.forClass(Scooter.class);
        verify(scooterRepository, times(1)).persist(scooterArgumentCaptor.capture());
        assertEquals(scooterArgumentCaptor.getValue().getStatus(), request.status());
    }

    @Test
    @DisplayName("It should be able to patch a scooter with serial number")
    void patchWithSerialNumber() {
        var scooter = ScooterTestObject.get();

        var request = new ScooterPatchDTO(BigDecimal.TEN, null, ScooterStatus.IN_USE.name());

        when(scooterRepository.findBySerialNumber(scooter.getSerialNumber()))
                .thenReturn(Uni.createFrom().item(scooter));

        var subscriber = scooterService.patchWithSerialNumber(scooter.getSerialNumber(), request)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();

        var scooterArgumentCaptor = ArgumentCaptor.forClass(Scooter.class);
        verify(scooterRepository, times(1)).persist(scooterArgumentCaptor.capture());
        assertEquals(scooterArgumentCaptor.getValue().getStatus(), request.status());
    }

    @Test
    @DisplayName("It should be able to find a scooter by id")
    void findById() {
        var scooter = ScooterTestObject.get();

        when(scooterRepository.findById(scooter.getId()))
                .thenReturn(Uni.createFrom().item(scooter));

        var subscriber = scooterService.findById(scooter.getId())
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();

        var idCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(scooterRepository, times(1)).findById(idCaptor.capture());
        assertEquals(idCaptor.getValue(), scooter.getId());
    }
}
