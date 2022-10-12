import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jdk.jfr.Name;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class eCommerce {
    //Variables
    static private String url_base = "webapi.segundamano.mx";
    static private String email = "test2022_agente@mailinator.com";
    static private String password = "54321";
    static private String user_name;
    static private String access_token;
    static private String account_id;
    static private String uuid;
    static public String ad_id;
    static public String alert_id;
    static public String address_id;

    @Name("Obtener Token")
    private String obtener_Token(){

        RestAssured.baseURI=String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        Response response = given()
                .log().all()
                .queryParam("lang","es")
                .auth().preemptive().basic(email,password)
                .post();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Cambiar el body a JSon
        JsonPath jsonResponse = response.jsonPath();

        String accesstoken =jsonResponse.get("access_token");
        System.out.println("Token en funcion: " + accesstoken);
        access_token = accesstoken;

        //Otras variables
        String accountID =jsonResponse.get("account.account_id");
        System.out.println("account id en funcion: " + accountID);
        account_id = accountID;

        //Asignar la variable uuid
        String uid = jsonResponse.get("account.uuid");
        System.out.println("uuid en funcion: " + uid);
        uuid = uid;

        String username =jsonResponse.get("account.name");
        System.out.println("username obtener: " + username);
        user_name = username;

        return access_token;

    }

    @Name("Obtener AdID")
    private String abtenerAdID() {

        String body_request = "{\"category\":\"8143\"," +
                "\"subject\":\"Te organizo tu evento YAY\"," +
                "\"body\":\"trabajamos todo tipo de eventos, desde bautizos hasta bodas y divorcio\"," +
                "\"region\":\"5\",\"municipality\":\"51\",\"area\":\"36611\",\"price\":\"20000\",\"phone_hidden\":\"true\",\"show_phone\":\"false\",\"contact_phone\":\"76013183\"}";

        RestAssured.baseURI=String.format("https://%s/v2/accounts/%s/up",url_base,uuid);

        Response response = given()
                .log().all()
                .header("Content-type","application/json")
                .header("Accept","application/json, text/plain, */*")
                .header("x-source","PHOENIX_DESKTOP")
                .body(body_request)
                .auth().preemptive().basic(uuid,access_token)
                .post();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Cambiar el body a JSon
        JsonPath jsonResponse = response.jsonPath();

        String adid =jsonResponse.get("data.ad.ad_id");
        System.out.println("ad id de obtenerAdID: " + adid);
        ad_id = adid;

        System.out.println("ad id de obtener ad_id: " + ad_id);
        return ad_id;

    }

    @Name("Obtener Alerta ID")
    private String obtenerAlertID() {

        String body_request = "{\"ad_listing_service_filters\":{\"region\":\"17\",\"category_lv0\":\"6000\",\"category_lv1\":\"6060\",\"sort\":\"price\"}}";

        RestAssured.baseURI=String.format("https://%s/alerts/v1/private/account/%s/alert",url_base,uuid);

        Response response = given()
                .log().all()
                .header("Accept","application/json, text/plain, */*")
                .auth().preemptive().basic(uuid,access_token)
                .body(body_request)
                .filter(new AllureRestAssured())
                .post();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Cambiar el body a JSon
        JsonPath jsonResponse = response.jsonPath();

        String alertid =jsonResponse.get("data.alert.id");
        System.out.println("alertid de obtener id alert: " + alertid);
        alert_id = alertid;

        System.out.println("alert_id de obtener ad_id: " + alert_id);
        return alert_id;

    }

    @Name("Obtener Address ID")
    private String obtenerAddressID() {

        String token = obtener_Token();
        System.out.println("Token: " + token);

        RestAssured.baseURI=String.format("https://%s/addresses/v1/create",url_base);

        Response response = given()
                .log().all()
                .formParam("contact","Agente de ventas")
                .formParam("phone","8776655443")
                .formParam("rfc","CAPL800101")
                .formParam("zipCode","45999")
                .formParam("exteriorInfo","Miguel Hidalgo 4232")
                .formParam("interiorInfo","2")
                .formParam("region","11")
                .formParam("municipality","300")
                .formParam("area","8094")
                .formParam("alias","La oficina")
                .header("Content-type","application/x-www-form-urlencoded")
                .header("Accept","application/json, text/plain, */*")
                .auth().preemptive().basic(uuid,access_token)
                .post();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Cambiar el body a JSon
        JsonPath jsonResponse = response.jsonPath();

        String addressid =jsonResponse.get("addressID");
        System.out.println("addressid de obtener: " + addressid);
        address_id = addressid;

        System.out.println("address_id de obtener addressid: " + address_id);
        return address_id;

    }

    @Test
    @Order(1)
    @DisplayName("Test case: Obtener categorias")
    @Severity(SeverityLevel.NORMAL)
    public void get_obtenerCategorias_200(){
        RestAssured.baseURI=String.format("https://%s/nga/api/v1.1/public/categories/filter?lang=es",url_base);

        Response response = given()
                .log().all()
                .queryParam("lang","es")
                .filter(new AllureRestAssured())
                .get();

        //Imprimir el body response
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Pruebas al menos 5 pruebas
        //Validar el status response
        assertEquals(200,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el body contenga la palabra ID
        assertTrue(body_response.contains("id"));
        assertTrue(body_response.contains("categories"));
        assertTrue(body_response.contains("all_label"));

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 3000);

        //Validar los headers
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(2)
    @DisplayName("Test case: Crear Uusario")
    @Severity(SeverityLevel.NORMAL)
    public void post_crearUsuario_401(){

        //Crear Usuario
        String new_user = "agente_ventas" + (Math.floor(Math.random()*987)) + "@mailinator.com";
        //String password = "12345";
        String bodyRequest = "{\"account\":{\"email\":\""+new_user+"\"}}";

        RestAssured.baseURI=String.format("https://%s/nga/api/v1.1/private/accounts?lang=es",url_base);

        Response response = given()
                .log().all()
                .queryParam("lang","es")
                .contentType("application/json")
                .auth().preemptive().basic(new_user,password)
                .body(bodyRequest)
                .post();

        //Imprimir el body response
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Validar el status response
        assertEquals(401,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el body contenga la palabra ID
        assertTrue(body_response.contains("error"));
        assertTrue(body_response.contains("code"));
        assertTrue(body_response.contains("ACCOUNT_VERIFICATION_REQUIRED"));

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 1800);

        //Validar los headers
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));
    }

    @Test
    @Order(3)
    @DisplayName("Test case: Obtener usuario")
    @Severity(SeverityLevel.NORMAL)
    public void post_ObtenerUsuario_200(){
        //Configurar
        String bodyRequest = "{\"account\":{\"email\":\""+email+"\"}}";

        //ejecucion
        RestAssured.baseURI=String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        Response response = given()
                .log().all()
                .queryParam("lang","es")
                .contentType("application/json")
                .auth().preemptive().basic(email,password)
                .body(bodyRequest)
                .post();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Validar el status response
        assertEquals(200,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el body contenga la palabra ID
        assertTrue(body_response.contains("access_token"));
        assertTrue(body_response.contains("account_id"));
        assertTrue(body_response.contains("test2022_agente@mailinator.com"));
        assertTrue(body_response.contains(email));

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 3000);

        //Validar los headers
        String headers_response = response.getHeaders().toString();

        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(4)
    @DisplayName("Test case: Editar usuario")
    @Severity(SeverityLevel.NORMAL)
    public void patch_EditarUsuario_200(){

        String token = obtener_Token();
        System.out.println("Token: " + token);

        System.out.println("Token de funcion: " + access_token);
        System.out.println("account id de funcion: " + account_id);
        System.out.println("uuid de funcion: " + uuid);

        String body_request = "{\"account\":{\"name\":\"Mariela\",\"phone\":\"1234321667\",\"locations\":[{\"code\":\"5\",\"key\":\"region\",\"label\":\"Baja California Sur\",\"locations\":[{\"code\":\"51\",\"key\":\"municipality\",\"label\":\"comondú\"}]}],\"professional\":false,\"phone_hidden\":false}}";

        //ejecucion
        RestAssured.baseURI=String.format("https://%s/nga/api/v1.1/%s",url_base,account_id);

        Response response = given()
                .log().all()
                .queryParam("lang","es")
                .header("Content-type","application/json")
                .header("Accept","application/json, text/plain, */*")
                .header("Origin","https://www.segundamano.mx")
                .header("Authorization","tag:scmcoord.com,2013:api " + access_token)
                .body(body_request)
                .filter(new AllureRestAssured())
                .patch();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Validar el status response
        assertEquals(200,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el body contenga la palabra ID
        assertTrue(body_response.contains("test2022_agente@mailinator.com"));
        assertTrue(body_response.contains("Baja California Sur"));

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 3800);

        //Validar los headers
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(5)
    @DisplayName("Test case: Crear una direccion")
    @Severity(SeverityLevel.NORMAL)
    public void post_CrearUnaDireccion_201(){

        String token = obtener_Token();
        System.out.println("Token: " + token);

        System.out.println("Token de funcion: " + access_token);
        System.out.println("account id de funcion: " + account_id);
        System.out.println("uuid de funcion: " + uuid);

        //ejecucion
        RestAssured.baseURI=String.format("https://%s/addresses/v1/create",url_base);

        Response response = given()
                .log().all()
                .formParam("contact","Agente de ventas")
                .formParam("phone","8776655443")
                .formParam("rfc","CAPL800101")
                .formParam("zipCode","45999")
                .formParam("exteriorInfo","Miguel Hidalgo 4232")
                .formParam("interiorInfo","2")
                .formParam("region","11")
                .formParam("municipality","300")
                .formParam("area","8094")
                .formParam("alias","La oficina")
                .header("Content-type","application/x-www-form-urlencoded")
                .header("Accept","application/json, text/plain, */*")
                .auth().preemptive().basic(uuid,access_token)
                .post();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Pruebas al menos 5 pruebas
        //Validar el status response
        assertEquals(201,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el body contenga la palabra ID
        assertTrue(body_response.contains("addressID"));

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 1800);

        //Validar los headers
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(6)
    @DisplayName("Test case: Crear un Anuncio")
    @Severity(SeverityLevel.NORMAL)
    public void post_CrearUnAnuncio_200(){

        String token = obtener_Token();
        System.out.println("Token: " + token);

        String body_request = "{\"category\":\"8143\"," +
                "\"subject\":\"Te organizo tu evento YAY\"," +
                "\"body\":\"trabajamos todo tipo de eventos, desde bautizos hasta bodas y divorcio\"," +
                "\"region\":\"5\",\"municipality\":\"51\",\"area\":\"36611\",\"price\":\"20000\",\"phone_hidden\":\"true\",\"show_phone\":\"false\",\"contact_phone\":\"76013183\"}";

        //ejecucion
        RestAssured.baseURI=String.format("https://%s/v2/accounts/%s/up",url_base,uuid);

        Response response = given()
                .log().all()
                .header("Content-type","application/json")
                .header("Accept","application/json, text/plain, */*")
                .header("x-source","PHOENIX_DESKTOP")
                .auth().preemptive().basic(uuid,access_token)
                .body(body_request)
                .filter(new AllureRestAssured())
                .post();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Validar el status response
        assertEquals(200,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el body contenga la palabra ID
        assertTrue(body_response.contains("Servicios"));

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 3800);

        //Validar los headers
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(7)
    @DisplayName("Test case: Editar Anuncio")
    @Severity(SeverityLevel.NORMAL)
    public void put_EditarAnuncio_200(){

        String token = obtener_Token();
        System.out.println("Token: " + token);

        String ad_idd = abtenerAdID();
        System.out.println("ad_id de la clase abtenerAdID: " + ad_idd);

        String body_request = "{\"category\":\"8143\",\"subject\":\"Organizamos tu evento\",\"body\":\"trabajamos todo tipo de eventos, desde bautizos hasta bodas. Pregunte sin compromiso. Hacemos cotizaciones yuju\",\"region\":\"5\",\"municipality\":\"51\",\"area\":\"36611\",\"price\":\"20000\",\"phone_hidden\":\"true\",\"show_phone\":\"false\",\"contact_phone\":\"76013183\"}";

        //ejecucion
        RestAssured.baseURI=String.format("https://%s/v2/accounts/%s/up/%s",url_base,uuid,ad_id);

        Response response = given()
                .log().all()
                .header("Content-type","application/json")
                .header("Accept","application/json, text/plain, */*")
                .header("x-source","PHOENIX_DESKTOP")
                .auth().preemptive().basic(uuid,access_token)
                .body(body_request)
                .filter(new AllureRestAssured())
                .put();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Validar el status response
        assertEquals(200,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el body contenga la palabra ID
        assertTrue(body_response.contains("ad_id"));
        assertTrue(body_response.contains("subject"));

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 3800);

        //Validar los headers
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(8)
    @DisplayName("Test case: Borrar Anuncio")
    @Severity(SeverityLevel.NORMAL)
    public void delete_BorarAnuncio_200(){

        String token = obtener_Token();
        System.out.println("Token: " + token);

        String ad_idd = abtenerAdID();
        System.out.println("ad_id de la clase abtenerAdID: " + ad_idd);

        String body_request = "{\"delete_reason\":{\"code\":\"0\"}}";

        //ejecucion
        RestAssured.baseURI=String.format("https://%s/nga/api/v1/%s/klfst/%s",url_base,account_id,ad_id);

        Response response = given()
                .log().all()
                .header("Content-type","application/json")
                .header("Accept","application/json, text/plain, */*")
                .header("Authorization","tag:scmcoord.com,2013:api " + access_token)
                .body(body_request)
                .filter(new AllureRestAssured())
                .delete();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Validar el status response
        assertEquals(403,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el body contenga la palabra ID
        assertTrue(body_response.contains("code"));
        assertTrue(body_response.contains("FORBIDDEN"));

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 3800);

        //Validar los headers
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(9)
    @DisplayName("Test case: Crear Alerta")
    @Severity(SeverityLevel.NORMAL)
    public void post_CrearAlerta_200(){

        String token = obtener_Token();
        System.out.println("Token: " + token);

        String body_request = "{\"ad_listing_service_filters\":{\"region\":\"13\",\"municipality\":\"359\",\"category_lv0\":\"4000\",\"category_lv1\":\"4080\",\"sort\":\"price\"}}";

        //ejecucion
        RestAssured.baseURI=String.format("https://%s/alerts/v1/private/account/%s/alert",url_base,uuid);


        Response response = given()
                .log().all()
                .header("Accept","application/json, text/plain, */*")
                .auth().preemptive().basic(uuid,access_token)
                .body(body_request)
                .filter(new AllureRestAssured())
                .post();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Validar el status response
        assertEquals(200,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el body contenga la palabra ID
        assertTrue(body_response.contains("alert"));
        assertTrue(body_response.contains("created_at"));

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 3800);

        //Validar los headers
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(10)
    @DisplayName("Test case: Borrar Alerta")
    @Severity(SeverityLevel.NORMAL)
    public void delete_BorrarAlerta_200(){

        String token = obtener_Token();
        System.out.println("Token: " + token);

        String alert_idd = obtenerAlertID();
        System.out.println("alert_id de la clase abtenerAlertID: " + alert_idd);

        //ejecucion
        RestAssured.baseURI=String.format("https://%s/alerts/v1/private/account/%s/alert/%s",url_base,uuid, alert_id);

        Response response = given()
                .log().all()
                .header("Accept","application/json, text/plain, */*")
                .auth().preemptive().basic(uuid,access_token)
                .filter(new AllureRestAssured())
                .delete();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Validar el status response
        assertEquals(200,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el body contenga la palabra ID
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("status"));

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 3800);

        //Validar los headers
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(11)
    @DisplayName("Test case: Seleccionar Favoritos")
    @Severity(SeverityLevel.NORMAL)
    public void post_SeleccionarFavoritos_200(){

        String token = obtener_Token();
        System.out.println("Token: " + token);

        String body_request = "{\"list_ids\":[939008284]}";

        //ejecucion
        RestAssured.baseURI=String.format("https://%s/favorites/v1/private/accounts/%s",url_base,uuid);

        Response response = given()
                .log().all()
                .header("Content-type","application/json")
                .header("Accept","application/json, text/plain, */*")
                .auth().preemptive().basic(uuid,access_token)
                .body(body_request)
                .filter(new AllureRestAssured())
                .post();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Validar el status response
        assertEquals(200,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el body contenga la palabra ID
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("added"));

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 3800);

        //Validar los headers
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(12)
    @DisplayName("Test case: Enviar Mensaje")
    @Severity(SeverityLevel.NORMAL)
    public void post_EnviarMensaje_204(){

        String token = obtener_Token();
        System.out.println("Token: " + token);

        String body_request = "{\"cc_sender\":true,\"dm_source\":\"favorites\",\"sub_source\":\"\",\"message\":{\"body\":\"¡Hola Me interesa tu anuncio ¿Sigue disponible?\"," +
                "\"email\":\""+email+"\",\"name\":\""+user_name+"\",\"phone\":\"\"}}";

        //ejecucion
        RestAssured.baseURI=String.format("https://%s/nga/api/v1.7/public/klfst/939008284/messages",url_base);

        Response response = given()
                .log().all()
                .queryParam("lang","es")
                .header("Content-type","application/json")
                .header("Accept","application/json, text/plain, */*")
                .auth().preemptive().basic(uuid,access_token)
                .body(body_request)
                .filter(new AllureRestAssured())
                .post();

        //Validar el status response
        assertEquals(204,response.getStatusCode());

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 3800);

        //Validar los headers DENY
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("DENY"));

        //Validar los headers nginx
        String headers_response2 = response.getHeaders().toString();
        assertTrue(headers_response2.contains("nginx"));

    }

    @Test
    @Order(13)
    @DisplayName("Test case: Quitar Seleccion de Favoritos")
    @Severity(SeverityLevel.NORMAL)
    public void delete_QuitarFavoritos_200(){

        String token = obtener_Token();
        System.out.println("Token: " + token);

        String body_request = "{\"list_ids\":[939008284]}";

        //ejecucion
        RestAssured.baseURI=String.format("https://%s/favorites/v1/private/accounts/%s",url_base,uuid);

        Response response = given()
                .log().all()
                .header("Content-type","application/json")
                .header("Accept","application/json, text/plain, */*")
                .auth().preemptive().basic(uuid,access_token)
                .body(body_request)
                .filter(new AllureRestAssured())
                .delete();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Validar el status response
        assertEquals(200,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el body contenga la palabra ID
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("deleted"));

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 3800);

        //Validar los headers
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(14)
    @DisplayName("Test case: Eliminar Direccion")
    @Severity(SeverityLevel.NORMAL)
    public void delete_BorrarDireccion_200(){

        String token = obtener_Token();
        System.out.println("Token: " + token);

        String alert_idd = obtenerAddressID();
        System.out.println("address_id de la clase obtenerAddressID: " + address_id);

        //ejecucion
        RestAssured.baseURI=String.format("https://%s/addresses/v1/delete/%s",url_base,address_id);

        Response response = given()
                .log().all()
                .header("Accept","application/json, text/plain, */*")
                .auth().preemptive().basic(uuid,access_token)
                .filter(new AllureRestAssured())
                .delete();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Validar el status response
        assertEquals(200,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el body contenga la palabra ID
        assertTrue(body_response.contains("message"));

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 3800);

    }

    @Test
    @Order(15)
    @DisplayName("Test case: Busqueda Rapida")
    @Severity(SeverityLevel.NORMAL)
    public void post_BusquedaRapida_200(){

        String token = obtener_Token();
        System.out.println("Token: " + token);

        String body_request = "{\"ad_listing_service_filters\":{\"q\":\"Tester\",\"region\":\"24\",\"category\":\"6040\"}}";

        //ejecucion
        RestAssured.baseURI=String.format("https://%s/urls/v1/public/ad-listing",url_base);

        Response response = given()
                .log().all()
                .header("Accept","application/json, text/plain, */*")
                .auth().preemptive().basic(uuid,access_token)
                .filter(new AllureRestAssured())
                .body(body_request)
                .post();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Validar el status response
        assertEquals(200,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el body contenga la palabra ID
        assertTrue(body_response.contains("data"));

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 3800);

        //Validar los headers
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

}
