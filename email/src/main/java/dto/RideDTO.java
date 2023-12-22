package dto;

import java.util.UUID;

public record RideDTO(UUID id, String name, String email, boolean isConfirmed) {

}
