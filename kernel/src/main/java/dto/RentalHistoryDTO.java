package dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record RentalHistoryDTO(
        UUID id,
        UUID scooterId,
        UUID userId,
        Date startDate,
        Date endDate,
        BigDecimal totalCost) {

}
