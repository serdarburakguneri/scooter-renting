package adapter;

import dto.RentalHistoryDTO;
import entity.RentalHistory;

public class RentalHistoryDTOAdapter {

    public static RentalHistoryDTO fromRentalHistory(RentalHistory rentalHistory) {
        return new RentalHistoryDTO(rentalHistory.getId(),
                rentalHistory.getScooterId(),
                rentalHistory.getUserId(),
                rentalHistory.getStartDate(),
                rentalHistory.getEndDate(),
                rentalHistory.getTotalCost());
    }
}
