package hexlet.code;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import io.javalin.Javalin;
import io.ebean.DB;
import io.ebean.Transaction;

import java.util.List;
import hexlet.code.model.Url;
import hexlet.code.model.query.QUrl;

class AppTest {

    private static Javalin app;
    private static Transaction transaction;
    private static String baseUrl;

    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        Unirest.config().defaultBaseUrl(baseUrl);
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

    @BeforeEach
    void beforeEach() {
        transaction = DB.beginTransaction();
    }

    @AfterEach
    void afterEach() {
        transaction.rollback();
    }

    @Test
    void testNewUrl() {
        HttpResponse<String> response = Unirest.get("/")
            .asString();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void testCreateUrl() {
        HttpResponse<String> response1 = Unirest.post("/urls")
            .field("url", "https://javalin.io/tutorials/testing")
            .asString();
        assertThat(response1.getStatus()).isEqualTo(302);
        assertThat(response1.getHeaders().getFirst("Location")).isEqualTo("/urls");
        HttpResponse<String> response2 = Unirest.get("/urls")
            .asString();
        assertThat(response2.getStatus()).isEqualTo(200);
        assertThat(response2.getBody()).contains("javalin.io");

        HttpResponse<String> response3 = Unirest.post("/urls")
            .field("url", "https://javalin.io/documentation#context")
            .asString();
        assertThat(response3.getStatus()).isEqualTo(409);

        HttpResponse<String> response4 = Unirest.post("/urls")
            .field("url", "abcd")
            .asString();
        assertThat(response4.getStatus()).isEqualTo(400);
    }

    @Test
    void testListUrl() {
        HttpResponse<String> response = Unirest.get("/urls")
            .asString();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void testShowUrl() {
        List<Url> listUrl = new QUrl()
            .findList();
        int min = 0;
        int max = listUrl.size() - 1;
        int randomIndex = (int) (Math.random() * (max - min)) + min;
        String id = listUrl.get(randomIndex).getId().toString();
        HttpResponse<String> response = Unirest.get("/urls/{id}")
            .routeParam("id", id)
            .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains(listUrl.get(randomIndex).getHost());
    }
}
