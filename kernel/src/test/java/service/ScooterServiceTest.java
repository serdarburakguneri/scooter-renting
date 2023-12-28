package service;

import dto.ScooterCreationDTO;
import dto.ScooterDTO;
import entity.Scooter;
import entity.ScooterStatus;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import jakarta.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import producer.ScooterMessageProducer;
import repository.ScooterRepository;

import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
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
    private ScooterMessageProducer scooterMessageProducer;

    @BeforeEach
    void setUp() {
        scooterRepository = mock(ScooterRepository.class);
        scooterMessageProducer = mock(ScooterMessageProducer.class);
        scooterService = new ScooterService(scooterRepository, scooterMessageProducer);
    }

    @Test
    @DisplayName("It should be able to register a scooter")
    void create() {
        var request = new ScooterCreationDTO("12345ABCDE", "Bird", "One");

        var scooter = new Scooter.Builder()
                .withSerialNumber(request.serialNumber())
                .withBrand(request.brand())
                .withModel(request.model())
                .withStatus(ScooterStatus.OUT_OF_SERVICE.name())
                .withBatteryLevel(BigDecimal.ZERO)
                .withLocation("0", "0")
                .build();

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
    @DisplayName("It should be able to validate if unlocking is possible for a scooter")
    void validateUnlocking() {

        var scooterId = UUID.randomUUID();

        var scooter = new Scooter.Builder()
                .withSerialNumber("1234ABC")
                .withBrand("Bird")
                .withModel("One")
                .withStatus(ScooterStatus.AVAILABLE.name())
                .withBatteryLevel(BigDecimal.valueOf(90))
                .withLocation("0", "0")
                .build();

        when(scooterRepository.findById(scooterId))
                .thenReturn(Uni.createFrom().item(scooter));

        var subscriber = scooterService.validateUnlocking(scooterId)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();
    }

    @Test
    @DisplayName("It should fail if a scooter with provided id can not be found")
    void validateUnlockingWhenScooterIsNotFound() {

        var scooterId = UUID.randomUUID();

        when(scooterRepository.findById(scooterId))
                .thenReturn(Uni.createFrom().nullItem());

        var subscriber = scooterService.validateUnlocking(scooterId)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(NotFoundException.class);
    }

    @Test
    @DisplayName("It should fail if a scooter does not have enough battery level")
    void validateUnlockingWhenScooterDoesNotHaveEnoughBatteryLevel() {

        var scooterId = UUID.randomUUID();

        var scooter = new Scooter.Builder()
                .withSerialNumber("1234ABC")
                .withBrand("Bird")
                .withModel("One")
                .withStatus(ScooterStatus.AVAILABLE.name())
                .withBatteryLevel(BigDecimal.valueOf(ScooterService.MIN_BATTERY_LEVEL_FOR_RIDE - 1L))
                .withLocation("0", "0")
                .build();

        when(scooterRepository.findById(scooterId))
                .thenReturn(Uni.createFrom().item(scooter));

        var subscriber = scooterService.validateUnlocking(scooterId)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class);
    }

    @DisplayName("It should fail if scooter is not available")
    @ParameterizedTest
    @EnumSource(value = ScooterStatus.class, mode = EXCLUDE, names = {"AVAILABLE"})
    void validateUnlockingWhenScooterIsNotAvailable(ScooterStatus scooterStatus) {

        var scooterId = UUID.randomUUID();

        var scooter = new Scooter.Builder()
                .withSerialNumber("1234ABC")
                .withBrand("Bird")
                .withModel("One")
                .withStatus(scooterStatus.name())
                .withBatteryLevel(BigDecimal.valueOf(90))
                .withLocation("0", "0")
                .build();

        when(scooterRepository.findById(scooterId))
                .thenReturn(Uni.createFrom().item(scooter));

        var subscriber = scooterService.validateUnlocking(scooterId)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class);
    }

    @Test
    @DisplayName("It should be able to request unlocking the scooter")
    void requestUnlock() {

        var scooterId = UUID.randomUUID();

        var scooter = new Scooter.Builder()
                .withSerialNumber("1234ABC")
                .withBrand("Bird")
                .withModel("One")
                .withStatus(ScooterStatus.AVAILABLE.name())
                .withBatteryLevel(BigDecimal.valueOf(90))
                .withLocation("0", "0")
                .build();

        when(scooterRepository.findById(scooterId))
                .thenReturn(Uni.createFrom().item(scooter));

        var subscriber = scooterService.requestUnlock(scooterId)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();

        var persistingCaptor = ArgumentCaptor.forClass(Scooter.class);
        verify(scooterRepository, times(1)).persist(persistingCaptor.capture());
        assertEquals(persistingCaptor.getValue().getStatus(), ScooterStatus.REQUESTED_FOR_RENT.name());

        var messagingCaptor = ArgumentCaptor.forClass(ScooterDTO.class);
        verify(scooterMessageProducer, times(1)).scooterUnlockRequested(messagingCaptor.capture());
        assertEquals(messagingCaptor.getValue().serialNumber(), scooter.getSerialNumber());
    }

}
