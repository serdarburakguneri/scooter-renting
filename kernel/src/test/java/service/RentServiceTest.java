package service;

import dto.RentRequestDTO;
import dto.ScooterDTO;
import entity.RentalHistory;
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
import repository.RentalHistoryRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("UnitTest")
public class RentServiceTest {

    private RentService rentService;
    private ScooterService scooterService;
    private RentalHistoryRepository repository;
    private ScooterMessageProducer scooterMessageProducer;

    @BeforeEach
    void setUp() {
        repository = mock(RentalHistoryRepository.class);
        scooterService = mock(ScooterService.class);
        scooterMessageProducer = mock(ScooterMessageProducer.class);
        rentService = new RentService(scooterService, repository, scooterMessageProducer);
    }

    @Test
    @DisplayName("It should be able to request renting a scooter")
    void requestRenting() {

        var scooterId = UUID.randomUUID();
        var userId = UUID.randomUUID().toString();
        var request = new RentRequestDTO(scooterId);

        var scooter = new Scooter.Builder()
                .withSerialNumber("1234ABC")
                .withBrand("Bird")
                .withModel("One")
                .withStatus(ScooterStatus.AVAILABLE.name())
                .withBatteryLevel(BigDecimal.valueOf(90))
                .withLocation("0", "0")
                .build();

        when(scooterService.findById(scooterId))
                .thenReturn(Uni.createFrom().item(scooter));

        var subscriber = rentService.requestRenting(request, userId)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();

        verify(repository, times(1)).persist(any(RentalHistory.class));
        verify(scooterMessageProducer, times(1)).scooterUnlockRequested(any(ScooterDTO.class));

    }

    @Test
    @DisplayName("It should fail if a scooter with provided id can not be found")
    void validateUnlockingWhenScooterIsNotFound() {

        var scooterId = UUID.randomUUID();
        var userId = UUID.randomUUID().toString();
        var request = new RentRequestDTO(scooterId);

        when(scooterService.findById(scooterId))
                .thenReturn(Uni.createFrom().nullItem());

        var subscriber = rentService.requestRenting(request, userId)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(NotFoundException.class);
    }

    @Test
    @DisplayName("It should fail if a scooter does not have enough battery level")
    void validateUnlockingWhenScooterDoesNotHaveEnoughBatteryLevel() {

        var scooterId = UUID.randomUUID();
        var userId = UUID.randomUUID().toString();
        var request = new RentRequestDTO(scooterId);

        var scooter = new Scooter.Builder()
                .withSerialNumber("1234ABC")
                .withBrand("Bird")
                .withModel("One")
                .withStatus(ScooterStatus.AVAILABLE.name())
                .withBatteryLevel(BigDecimal.valueOf(RentService.MIN_BATTERY_LEVEL_FOR_RIDE - 1L))
                .withLocation("0", "0")
                .build();

        when(scooterService.findById(scooterId))
                .thenReturn(Uni.createFrom().item(scooter));

        var subscriber = rentService.requestRenting(request, userId)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class);
    }

    @DisplayName("It should fail if scooter is not available")
    @ParameterizedTest
    @EnumSource(value = ScooterStatus.class, mode = EXCLUDE, names = {"AVAILABLE"})
    void validateUnlockingWhenScooterIsNotAvailable(ScooterStatus scooterStatus) {

        var scooterId = UUID.randomUUID();
        var userId = UUID.randomUUID().toString();
        var request = new RentRequestDTO(scooterId);

        var scooter = new Scooter.Builder()
                .withSerialNumber("1234ABC")
                .withBrand("Bird")
                .withModel("One")
                .withStatus(scooterStatus.name())
                .withBatteryLevel(BigDecimal.valueOf(90))
                .withLocation("0", "0")
                .build();

        when(scooterService.findById(scooterId))
                .thenReturn(Uni.createFrom().item(scooter));

        var subscriber = rentService.requestRenting(request, userId)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class);
    }

    @Test
    @DisplayName("It should be able to request unlocking the scooter")
    void requestUnlock() {

        var scooterId = UUID.randomUUID();
        var userId = UUID.randomUUID().toString();
        var request = new RentRequestDTO(scooterId);

        var scooter = new Scooter.Builder()
                .withSerialNumber("1234ABC")
                .withBrand("Bird")
                .withModel("One")
                .withStatus(ScooterStatus.AVAILABLE.name())
                .withBatteryLevel(BigDecimal.valueOf(90))
                .withLocation("0", "0")
                .build();

        when(scooterService.findById(scooterId))
                .thenReturn(Uni.createFrom().item(scooter));

        var subscriber = rentService.requestRenting(request, userId)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();

        var persistingCaptor = ArgumentCaptor.forClass(Scooter.class);
        verify(scooterService, times(1)).update(persistingCaptor.capture());
        assertEquals(persistingCaptor.getValue().getStatus(), ScooterStatus.REQUESTED_FOR_RENT.name());

        var messagingCaptor = ArgumentCaptor.forClass(ScooterDTO.class);
        verify(scooterMessageProducer, times(1)).scooterUnlockRequested(messagingCaptor.capture());
        assertEquals(messagingCaptor.getValue().serialNumber(), scooter.getSerialNumber());
    }


}
