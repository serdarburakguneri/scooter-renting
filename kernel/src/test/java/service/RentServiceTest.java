package service;

import dto.RentRequestDTO;
import entity.RentalHistory;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import repository.RentalHistoryRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Tag("UnitTest")
public class RentServiceTest {

    private RentService rentService;
    private ScooterService scooterService;
    private RentalHistoryRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(RentalHistoryRepository.class);
        scooterService = mock(ScooterService.class);
        rentService = new RentService(scooterService, repository);
    }

    @Test
    @DisplayName("It should be able to request renting a scooter")
    void requestRenting() {

        var scooterId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var request = new RentRequestDTO(scooterId, userId);

        var subscriber = rentService.requestRenting(request)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();

        verify(scooterService, times(1)).validateUnlocking(any());
        verify(repository, times(1)).persist(any(RentalHistory.class));
        verify(scooterService, times(1)).requestUnlock(any());
        //TODO: There are good tests for ScooterService but let's also enrich this one
    }


}
