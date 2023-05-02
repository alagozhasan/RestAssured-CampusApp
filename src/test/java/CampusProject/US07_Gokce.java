package CampusProject;

import com.github.javafaker.Faker;
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
public class US07_Gokce {

    RequestSpecification reqSpec;
    Faker faker=new Faker();

    String locationName;

    Map<String, String> locations=new HashMap<>();
    String locationID;
    @BeforeClass
    public void Login(){

        baseURI="https://test.mersys.io";

        Map<String, String> userCredential=new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");

        Cookies cookies=
                given()

                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("/auth/login")

                        .then()
                        .statusCode(200)
                        .extract().response().getDetailedCookies()
                ;

        reqSpec=new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build()
        ;
    }

    @Test
    public void createLocation(){

        locationName =faker.address().cityName()+faker.number().digits(2);
        locations.put("name", locationName);
        locations.put("shortName",faker.funnyName().name());
        locations.put("type", "CLASS");
        locations.put("capacity", faker.number().digits(2));
        locations.put("school", "6390f3207a3bcb6a7ac977f9");

        locationID=
        given()
                .spec(reqSpec)
                .body(locations)
                .log().body()

                .when()
                .post("/school-service/api/location")

                .then()
                .log().body()
                .statusCode(201)
                .extract().path("id")

                ;

        System.out.println("locationID = " + locationID);
    }
    @Test (dependsOnMethods = "createLocation")
    public void createLocationNeg(){
        locations.put("name", locationName);
        locations.put("shortName",faker.funnyName().name());
        locations.put("type", "CLASS");
        locations.put("capacity", faker.number().digits(2));
        locations.put("school", "6390f3207a3bcb6a7ac977f9");

        given()
                .spec(reqSpec)
                .body(locations)
                .log().body()

                .when()
                .post("/school-service/api/location")

                .then()
                .log().body()
                .statusCode(400)

        ;
    }
    @Test (dependsOnMethods = "createLocation")
    public void updateLocation(){

        locations.put("id", locationID);
        locationName="Gökçe"+faker.number().digits(4);
        locations.put("name", locationName);

        given()
                .spec(reqSpec)
                .body(locations)
                .log().body()

                .when()
                .put("/school-service/api/location")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(locationName))
        ;

    }



    @Test(dependsOnMethods = "updateLocation")
    public void deleteLocation(){

        given()

                .spec(reqSpec)
                .pathParam("locationID", locationID)
                .log().uri()
                .log().body()

                .when()
                .delete("/school-service/api/location/{locationID}")

                .then()
                .log().body()
                .statusCode(200)
                ;
    }



    @Test (dependsOnMethods = "deleteLocation" )
    public void deleteLocationNeg(){

        given()

                .spec(reqSpec)
                .pathParam("locationID", locationID)
                .log().uri()

                .when()
                .delete("/school-service/api/location/{locationID}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("School Location not found"))
        ;


    }


}
