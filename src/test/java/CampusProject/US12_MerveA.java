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

public class US12_MerveA {
    Faker faker=new Faker();

    String nationalityName;

    String nationalityID;
    RequestSpecification recSpec;


    @BeforeClass
    public void Login()  {
        baseURI="https://test.mersys.io";

        Map<String,String> userCredential=new HashMap<>();
        userCredential.put("username","turkeyts");
        userCredential.put("password","TechnoStudy123");
        userCredential.put("rememberMe","true");
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

        recSpec= new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createNationality(){
        Map<String,String>nationality=new HashMap<>();
        nationalityName="Merve Arslan";
        nationality.put("name",nationalityName);




        nationalityID=
                given()
                        .spec(recSpec)
                        .body(nationality)
                        .log().body()

                        .when()
                        .post("/school-service/api/nationality")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")

        ;
        System.out.println("nationality = " + nationality);

    }
    @Test(dependsOnMethods = "createNationality")
    public void createNationalityNegative(){

        Map<String,String>nationality=new HashMap<>();
        nationalityName="Merve Arslan";
        nationality.put("name",nationalityName);

        given()
                .spec(recSpec)
                .body(nationality)
                .log().body()

                .when()
                .post("/school-service/api/nationality")

                .then()
                .log().body()
                .statusCode(400)
                .body("message",containsString("already"))
        ;

    }
    @Test(dependsOnMethods = "createNationalityNegative" )
    public void updateNationality(){
        Map<String,Object>nationality=new HashMap<>();
        nationality.put("id",nationalityID);
        nationalityName="Armin Arslan";
        nationality.put("name",nationalityName);
        given()
                .spec(recSpec)
                .body(nationality)
                .log().body()
                .when()
                .put("/school-service/api/nationality")
                .then()
                .log().body()
                .statusCode(200)
                .body("name",equalTo(nationalityName))
        ;
    }

    @Test(dependsOnMethods = "updateNationality")
    public void deleteNationality(){
        given()
                .spec(recSpec)
                .pathParam("id",nationalityID)
                .log().uri()
                .when()
                .delete("/school-service/api/nationality/{id}")
                .then()
                .log().body()
                .statusCode(200)
        ;
    }



    @Test(dependsOnMethods = "deleteNationality")
    public void deleteNationalityNegative(){
        given()
                .spec(recSpec)
                .pathParam("id",nationalityID)
                .log().uri()
                .when()
                .delete("/school-service/api/nationality/{id}")
                .then()
                .log().body()
                .statusCode(400)
                .body("message",equalTo("Nationality not  found"))
        ;
    }





}
