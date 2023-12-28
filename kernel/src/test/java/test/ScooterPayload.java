package test;

import io.vertx.core.json.JsonObject;

public class ScooterPayload {

    public static JsonObject generate() {
        var serialNumber = "1234ABCD";
        var brand = "Bird";
        var model = "One";

        return new JsonObject()
                .put("serialNumber", serialNumber)
                .put("brand", brand)
                .put("model", model);
    }

}
