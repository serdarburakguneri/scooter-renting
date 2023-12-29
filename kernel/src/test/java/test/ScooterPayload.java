package test;

import entity.Scooter;
import entity.ScooterStatus;
import io.vertx.core.json.JsonObject;
import java.math.BigDecimal;

public class ScooterPayload {

    public static JsonObject create() {
        var serialNumber = "1234ABCD";
        var brand = "Bird";
        var model = "One";

        return new JsonObject()
                .put("serialNumber", serialNumber)
                .put("brand", brand)
                .put("model", model);
    }

    public static JsonObject patch() {
        var batteryLevel = BigDecimal.valueOf(99.999);
        var status = ScooterStatus.AVAILABLE;
        var location = new Scooter.Location();
        location.setLatitude("59.334591");
        location.setLongitude("18.063240");

        return new JsonObject()
                .put("batteryLevel", batteryLevel)
                .put("status", status)
                .put("location", location);
    }

}
