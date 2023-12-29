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

    @Test
    @DisplayName("POST should return 400 when payload is not valid")
    @TestSecurity(user = "testUser", roles = {UserRole.USER})
    void testRequestRentingWhenPayloadIsNotValid() {

        var requestBody = new JsonObject()
                .put("scooterId", null);

        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody.toString())
                .post("/rent")
                .then()
                .statusCode(StatusCode.BAD_REQUEST);
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

        var scooterCreationRequest = ScooterPayload.create();

        var scooterCreated = given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .body(scooterCreationRequest.toString())
                .post("/scooter")
                .then()
                .statusCode(StatusCode.CREATED)
                .extract()
                .as(ScooterDTO.class);

        var scooterPatchRequest = ScooterPayload.patch();

        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .body(scooterPatchRequest.toString())
                .patch("scooter/" + scooterCreated.id())
                .then()
                .statusCode(StatusCode.OK);

        var rentRequest = RentPayload.generate(scooterCreated.id());

        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .body(rentRequest.toString())
                .post("/rent")
                .then()
                .statusCode(StatusCode.OK)
                .body("scooterId", is(scooterCreated.id().toString()));
    }

}
