package resource;

import dto.ScooterDTO;
import entity.UserRole;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.vertx.core.json.JsonObject;
import jakarta.ws.rs.core.MediaType;
import java.util.UUID;
import java.util.stream.Stream;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.IntegrationTest;
import test.RentPayload;
import test.ScooterPayload;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class RentResourceTest extends IntegrationTest {

    @ParameterizedTest
    @MethodSource("getBadRequestInputs")
    @DisplayName("POST should return 400 when payload is not valid")
    @TestSecurity(user = "testUser", roles = {UserRole.USER})
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

    @Test
    @DisplayName("POST should return 401 for non authenticated requests")
    void testRequestRentingWithoutAuthentication() {
        var requestBody = new JsonObject()
                .put("scooterId", UUID.randomUUID())
                .put("userId", UUID.randomUUID());

        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody.toString())
                .post("/rent")
                .then()
                .statusCode(StatusCode.UNAUTHORIZED);
    }

    @Test
    @DisplayName("POST should return 200")
    @TestSecurity(user = "testUser", roles = {UserRole.ADMIN})
    void testRequestRenting() {

        var scooterCreationRequest = ScooterPayload.generate();

        var scooterCreated = given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .body(scooterCreationRequest.toString())
                .post("/scooter")
                .then()
                .statusCode(StatusCode.CREATED)
                .extract()
                .as(ScooterDTO.class);

        var rentRequest = RentPayload.generate(scooterCreated.id());

        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .body(rentRequest.toString())
                .post("/rent")
                .then()
                .statusCode(StatusCode.OK)
                .body("id", is(scooterCreated.id()));

        //TODO: need to implement status updating first. By default, scooters are out of service when they are created
    }

}
