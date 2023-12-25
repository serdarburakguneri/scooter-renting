package resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.vertx.core.json.JsonObject;
import jakarta.ws.rs.core.MediaType;
import java.util.stream.Stream;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@Tag("IntegrationTest")
@TestMethodOrder(OrderAnnotation.class)
public class ScooterResourceTest {

    @ParameterizedTest
    @MethodSource("getBadRequestInputs")
    @DisplayName("POST should return 400 when payload is not valid")
    @TestSecurity(user = "testUser", roles = {"admin"})
    @Order(1)
    void testCreateScooterWhenSerialNumberIsEmpty(String serialNumber, String brand, String model) {

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
    @TestSecurity(user = "testUser", roles = {"user"})
    @Order(2)
    void testCreateScooterForRegularUsers() {
        var serialNumber = "1234ABCD";
        var brand = "Bird";
        var model = "One";

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
                .statusCode(StatusCode.FORBIDDEN);
    }

    @Test
    @DisplayName("POST should return 401 for non authenticated requests")
    @Order(3)
    void testCreateScooterWithoutAuthentication() {
        var serialNumber = "1234ABCD";
        var brand = "Bird";
        var model = "One";

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
                .statusCode(StatusCode.UNAUTHORIZED);
    }

    @Test
    @DisplayName("POST should return 201")
    @TestSecurity(user = "testUser", roles = {"admin"})
    @Order(4)
    void testCreateScooter() {
        var serialNumber = "1234ABCD";
        var brand = "Bird";
        var model = "One";

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
                .statusCode(StatusCode.CREATED)
                .body("serialNumber", is(serialNumber))
                .body("brand", is(brand))
                .body("model", is(model));
    }

}
