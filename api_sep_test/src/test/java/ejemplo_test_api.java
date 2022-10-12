import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class ejemplo_test_api {

    @Test
    public void api_test(){
        //URL y URI
        RestAssured.baseURI = String.format("https://reqres.in/api/users?page=1");

        Response response = given()
                .log().all()
                .headers("Accept","*/*")
                .get();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Body " + body_response);

        String responseCode = String.format(String.valueOf(response.getStatusCode()));
        System.out.println("Status code: " + responseCode);

        //JUnit 5 (al menos 5 pruebas)

        //Validar el status response
        assertEquals(200,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el bodye contenga la palabra ID
        assertTrue(body_response.contains("id"));

        //validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 800);

        //Validar los headers
        String headers_response = response.getHeaders().toString();
        System.out.println("Las cabeceras" + headers_response);

        assertTrue(headers_response.contains("aplication/json"));

    }

    @Test
    public void add_pet_test(){
        RestAssured.baseURI = String.format("https://petstore.swagger.io/v2/pet");

        String bodyRequest = "{\n" +
                "  \"category\": {\n" +
                "    \"id\": 6723439,\n" +
                "    \"name\": \"Perro de raza chica\"\n" +
                "  },\n" +
                "  \"name\": \"Yesenia\",\n" +
                "  \"photoUrls\": [\n" +
                "    \"string\"\n" +
                "  ],\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"id\": 0,\n" +
                "      \"name\": \"manchado\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"available\"\n" +
                "}";

        Response response=given()
                .log().all()
                .headers("Accept","application/json")
                .headers("Content-Type","application/json")
                .body(bodyRequest)
                .post();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Body " + body_response);

        String responseCode = String.format(String.valueOf(response.getStatusCode()));
        System.out.println("Status code: " + responseCode);
    }
}
