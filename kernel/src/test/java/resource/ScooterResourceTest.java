package resource;

import entity.UserRole;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.vertx.core.json.JsonObject;
import jakarta.ws.rs.core.MediaType;
import java.util.stream.Stream;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.IntegrationTest;
import test.ScooterPayload;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class ScooterResourceTest extends IntegrationTest {

    @ParameterizedTest
    @MethodSource("getBadRequestInputs")
    @DisplayName("POST should return 400 when payload is not valid")
    @TestSecurity(user = "testUser", roles = {UserRole.ADMIN})
    void testCreateScooterWhenPayloadIsNotValid(String serialNumber, String brand, String model) {

        var requestBody = new JsonObject()
                .put("serialNumber", serialNumber)
                .put("brand", brand)
                .put("model", model);

        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody.toString())
                .post("/scooter")
                .then()
                .statusCode(StatusCode.BAD_REQUEST);
    }

    static Stream<Arguments> getBadRequestInputs() {
        return Stream
                .of(
                        Arguments.of(null, null, null),
                        Arguments.of("", "Bird", "One"),
                        Arguments.of("1234ABCD", "", "One"),
                        Arguments.of("1234ABCD", "Bird", "")
                );
    }

    @Test
    @DisplayName("POST should return 403 for non admin users")
    @TestSecurity(user = "testUser", roles = {UserRole.USER})
    void testCreateScooterForRegularUsers() {

        var requestBody = ScooterPayload.generate();

        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody.toString())
                .post("/scooter")
                .then()
                .statusCode(StatusCode.FORBIDDEN);
    }

    @Test
    @DisplayName("POST should return 401 for non authenticated requests")
    void testCreateScooterWithoutAuthentication() {

        var requestBody = ScooterPayload.generate();

        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody.toString())
                .post("/scooter")
                .then()
                .statusCode(StatusCode.UNAUTHORIZED);
    }

    @Test
    @DisplayName("POST should return 201")
    @TestSecurity(user = "testUser", roles = {UserRole.ADMIN})
    void testCreateScooter() {
        var requestBody = ScooterPayload.generate();

        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody.toString())
                .post("/scooter")
                .then()
                .statusCode(StatusCode.CREATED)
                .body("serialNumber", is(requestBody.getValue("serialNumber")))
                .body("brand", is(requestBody.getValue("brand")))
                .body("model", is(requestBody.getValue("model")));
    }

    @Test
    @DisplayName("GET should return 401 for non authenticated requests")
    void testListScootersWithoutAuthentication() {
        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .get("/scooter")
                .then()
                .statusCode(StatusCode.UNAUTHORIZED);
    }

    @Test
    @DisplayName("GET should return 200 for admins")
    @TestSecurity(user = "testUser", roles = {UserRole.ADMIN})
    void testListScootersForAdmins() {

        var requestBody = ScooterPayload.generate();

        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody.toString())
                .post("/scooter")
                .then()
                .statusCode(StatusCode.CREATED);

        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .get("/scooter")
                .then()
                .statusCode(StatusCode.OK)
                .body("size()", equalTo(1));

    }

    /*
    TODO: I should re handle this test, by providing admin auth for creation endpoint or simply persisting an entity with dbHelper
    @Test
    @DisplayName("GET should return 200 for regular users")
    @TestSecurity(user = "testUser", roles = {UserRole.USER})
    void testListScootersForRegularUsers() {

        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .get("/scooter")
                .then()
                .statusCode(StatusCode.OK)
                .body("size()", equalTo(1));
    }
     */

}
