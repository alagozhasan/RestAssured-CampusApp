package CampusProject;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class US09_Resul {

    RequestSpecification reqSpec;
    String id;
    String name = "Ress61";
    String iban = "61";
    String currency = "TRY";
    String schoolId = "6390f3207a3bcb6a7ac977f9";

    Map<String, String> documentType =new HashMap<>();

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
    public void createBankAccount(){

        documentType.put("name", name);
        documentType.put("iban", iban);
        documentType.put("currency",currency);
        documentType.put("schoolId",schoolId);

        id =
                given()
                        .spec(reqSpec)
                        .body(documentType)
                        //.log().body()

                        .when()
                        .post("/school-service/api/bank-accounts")

                        .then()
                        //.log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
    }

    @Test (dependsOnMethods = "createBankAccount")
    public void createBankAccountNeg(){

        documentType.put("name", name);
        documentType.put("iban", iban);
        documentType.put("currency",currency);
        documentType.put("schoolId",schoolId);

        given()
                .spec(reqSpec)
                .body(documentType)
                //.log().body()

                .when()
                .post("/school-service/api/bank-accounts")

                .then()
                //.log().body()
                .statusCode(400)
        ;
    }

    @Test (dependsOnMethods = "createBankAccount")
    public void updateBankAccount(){

        name="Ress6161";
        documentType.put("id",id);
        documentType.put("name", name);
        documentType.put("iban", iban);
        documentType.put("currency",currency);
        documentType.put("schoolId",schoolId);

        given()
                .spec(reqSpec)
                .body(documentType)
                //.log().body()

                .when()
                .put("/school-service/api/bank-accounts")

                .then()
                //.log().body()
                .statusCode(200)
                .body("name", equalTo(name))
        ;
    }

    @Test(dependsOnMethods = "updateBankAccount")
    public void deleteBankAccount(){

        given()

                .spec(reqSpec)
                .pathParam("id", id)
                //.log().body()

                .when()
                .delete("/school-service/api/bank-accounts/{id}")

                .then()
                //.log().body()
                .statusCode(200)
        ;
    }



    @Test (dependsOnMethods = "deleteBankAccount" )
    public void deleteBankAccountNeg(){

        given()

                .spec(reqSpec)
                .pathParam("id", id)
                //.log().uri()

                .when()
                .delete("/school-service/api/bank-accounts/{id}")

                .then()
                //.log().body()
                .statusCode(400)
                .body("message", equalTo("Please, bank account must be exist"))
        ;
    }
}
