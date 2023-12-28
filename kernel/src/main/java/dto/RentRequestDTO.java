package dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RentRequestDTO(
        @NotNull(message = "Id of the scooter can not be empty.")
        UUID scooterId) {

}
