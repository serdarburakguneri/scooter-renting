package adapter;

import dto.ScooterCreationDTO;
import dto.ScooterPatchDTO;
import entity.Scooter;
import entity.ScooterStatus;
import java.math.BigDecimal;
import adapter.util.PatchUtil;

public class ScooterAdapter {

    public static Scooter fromScooterCreationDTO(ScooterCreationDTO request) {
        return new Scooter.Builder()
                .withSerialNumber(request.serialNumber())
                .withBrand(request.brand())
                .withModel(request.model())
                .withStatus(ScooterStatus.OUT_OF_SERVICE.name())
                .withBatteryLevel(BigDecimal.ZERO)
                .withLocation("0", "0")
                .build();
    }

    public static Scooter fromScooterPatchDTO(ScooterPatchDTO request, Scooter scooter) {
        PatchUtil.updateIfChanged(scooter::setBatteryLevel, request.batteryLevel(), scooter::getBatteryLevel);
        PatchUtil.updateIfChanged(scooter::setStatus, request.status(), scooter::getStatus);

        var location = scooter.getLocation();
        PatchUtil.updateIfChanged(location::setLatitude, request.location().latitude(), location::getLatitude);
        PatchUtil.updateIfChanged(location::setLongitude, request.location().longitude(), location::getLongitude);

        return scooter;
    }
}
