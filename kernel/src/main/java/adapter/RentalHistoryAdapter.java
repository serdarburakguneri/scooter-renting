package adapter;

import dto.RentRequestDTO;
import entity.RentalHistory;
import entity.RentalStatus;

public class RentalHistoryAdapter {

    public static RentalHistory fromRentRequestDTO(RentRequestDTO request) {
        return new RentalHistory.Builder()
                .withScooterId(request.scooterId())
                .withUserId(request.userId())
                .withStatus(RentalStatus.REQUESTED)
                .build();
    }

}
