package test;

import io.vertx.core.json.JsonObject;
import java.util.UUID;

public class RentPayload {

    public static JsonObject generate(UUID scooterId) {
        return new JsonObject().put("scooterId", scooterId);
    }
}
