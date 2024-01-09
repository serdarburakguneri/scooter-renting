package test;

import entity.Scooter;
import entity.ScooterStatus;
import java.math.BigDecimal;

public class ScooterTestObject {

    public static Scooter get() {
        return new Scooter.Builder()
                .withSerialNumber("1234ABCD")
                .withBrand("Bird")
                .withModel("One")
                .withStatus(ScooterStatus.AVAILABLE.name())
                .withBatteryLevel(BigDecimal.valueOf(90.99))
                .withLocation("0", "0")
                .build();
    }

}
