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

public class US03_Resul {

    RequestSpecification reqSpec;
    String id;
    String name = "Ress61";
    String [] attachmentStages = {"STUDENT_REGISTRATION"};

    String schoolId = "6390f3207a3bcb6a7ac977f9";
    Map<String, Object> documentType =new HashMap<>();

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
    public void createDocumentType(){

        documentType.put("name", name);
        documentType.put("schoolId", schoolId);
        documentType.put("attachmentStages",attachmentStages);

        id =
                given()
                        .spec(reqSpec)
                        .body(documentType)
                        //.log().body()

                        .when()
                        .post("/school-service/api/attachments/create")

                        .then()
                        //.log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
    }

    @Test (dependsOnMethods = "createDocumentType")
    public void createDocumentTypeNeg(){

        documentType.put("name", name);
        documentType.put("schoolId", schoolId);
        documentType.put("attachmentStages",attachmentStages);

        given()
                .spec(reqSpec)
                .body(documentType)
                //.log().body()

                .when()
                .post("/school-service/api/attachments/create")

                .then()
                //.log().body()
                .statusCode(201) // Bug
        ;
    }

    @Test (dependsOnMethods = "createDocumentType")
    public void updateDocumentType(){

        name="Ress6161";
        documentType.put("id",id);
        documentType.put("name", name);
        documentType.put("schoolId", schoolId);
        documentType.put("attachmentStages",attachmentStages);

        given()
                .spec(reqSpec)
                .body(documentType)
                //.log().body()

                .when()
                .put("/school-service/api/attachments")

                .then()
                //.log().body()
                .statusCode(200)
                .body("name", equalTo(name))
        ;
    }

    @Test(dependsOnMethods = "updateDocumentType")
    public void deleteDocumentType(){

        given()

                .spec(reqSpec)
                .pathParam("id", id)
                //.log().body()

                .when()
                .delete("/school-service/api/attachments/{id}")

                .then()
                //.log().body()
                .statusCode(200)
        ;
    }



    @Test (dependsOnMethods = "deleteDocumentType" )
    public void deleteDocumentTypeNeg(){

        given()

                .spec(reqSpec)
                .pathParam("id", id)
                //.log().uri()

                .when()
                .delete("/school-service/api/attachments/{id}")

                .then()
                //.log().body()
                .statusCode(400)
                .body("message", equalTo("Attachment Type not found"))
        ;
    }
}
