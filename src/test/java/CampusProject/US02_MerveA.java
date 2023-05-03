package CampusProject;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class US02_MerveA {
    String attestationsName;
    String attestationsID;

    RequestSpecification recSpec;


    @BeforeClass
    public void Login() {
        baseURI = "https://test.mersys.io";

        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");
        Cookies cookies =

                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("/auth/login")

                        .then()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        recSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createAttestations() {
        Map<String, String> attestations = new HashMap<>();
        attestationsName = "Merve Arslan";
        attestations.put("name", attestationsName);

        attestationsID =
                given()
                        .spec(recSpec)
                        .body(attestations)
                        .log().body()

                        .when()
                        .post("/school-service/api/attestation")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
    }

    @Test(dependsOnMethods = "createAttestations")
    public void createAttestationsNegative() {

        Map<String, String> attestations = new HashMap<>();
        attestationsName = "Merve Arslan";
        attestations.put("name", attestationsName);

        given()
                .spec(recSpec)
                .body(attestations)
                .log().body()

                .when()
                .post("/school-service/api/attestation")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }

    @Test(dependsOnMethods = "createAttestationsNegative")
    public void updateAttestations() {
        Map<String, Object> attestations = new HashMap<>();
        attestations.put("id", attestationsID);
        attestationsName = "Armin Arslan";
        attestations.put("name", attestationsName);
        given()
                .spec(recSpec)
                .body(attestations)
                //.log().body()
                .when()
                .put("/school-service/api/attestation")
                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(attestationsName))
        ;
    }

    @Test(dependsOnMethods = "updateAttestations")
    public void deleteAttestations() {
        given()
                .spec(recSpec)
                .pathParam("id", attestationsID)
                .log().uri()
                .when()
                .delete("/school-service/api/attestation/{id}")
                .then()
                .log().body()
                .statusCode(204)
        ;
    }


    @Test(dependsOnMethods = "deleteAttestations")
    public void deleteAttestationsNegative() {
        given()
                .spec(recSpec)
                .pathParam("id", attestationsID)
                .log().uri()
                .when()
                .delete("/school-service/api/attestation/{id}")
                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("attestation not found"))
        ;
    }
}
