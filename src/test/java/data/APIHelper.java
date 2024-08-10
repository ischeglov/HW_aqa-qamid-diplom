package data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

public class APIHelper {

    private static RequestSpecification requestSpecification = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(8080)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .build();

    public static Response paymentByDebitCard(DataHelper.CardInfo cardInfo, int statusCode) {
        return given()
                .spec(requestSpecification)
                .body(cardInfo)
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(statusCode)
                .extract().response();
    }
}
