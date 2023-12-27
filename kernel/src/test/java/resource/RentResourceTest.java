package resource;

import entity.UserRole;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.vertx.core.json.JsonObject;
import jakarta.ws.rs.core.MediaType;
import java.util.UUID;
import java.util.stream.Stream;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.RestAssured.given;

@QuarkusTest
@Tag("IntegrationTest")
public class RentResourceTest {

    @ParameterizedTest
    @MethodSource("getBadRequestInputs")
    @DisplayName("POST should return 400 when payload is not valid")
    @TestSecurity(user = "testUser", roles = {UserRole.USER})
    @Order(1)
    void testRequestRentingWhenPayloadIsNotValid(UUID scooterId, UUID userId) {

        var requestBody = new JsonObject()
                .put("scooterId", scooterId)
                .put("userId", userId);

        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody.toString())
                .post("/rent")
                .then()
                .statusCode(StatusCode.BAD_REQUEST);
    }

    static Stream<Arguments> getBadRequestInputs() {
        return Stream
                .of(
                        Arguments.of(null, null),
                        Arguments.of(UUID.randomUUID(), null),
                        Arguments.of(null, UUID.randomUUID())
                );
    }

}
