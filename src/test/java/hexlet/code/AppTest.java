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

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import java.util.List;
import hexlet.code.model.UrlCheck;
import hexlet.code.model.Url;
import hexlet.code.model.query.QUrl;

class AppTest {

    private static Javalin app;
    private static Transaction transaction;
    private static MockWebServer mockServer;
    private static MockResponse mockResponse;
    private static String serverBaseUrl;
    private static String mockHost;
    private static int mockPort;
    private static String body;

    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        String baseUrl = "http://localhost:" + port;
        Unirest.config().defaultBaseUrl(baseUrl);

        Paths.get(".").toAbsolutePath().normalize().toString();
        try {
            String workDir = Paths.get(".").toAbsolutePath().normalize().toString();
            body = Files.lines(Paths.get(workDir + "/src/test/resources/test.html"))
                .collect(Collectors.joining());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mockServer = new MockWebServer();
        mockResponse = new MockResponse()
            .addHeader("Content-Type", "text/html;charset=utf-8")
            .setBody(body)
            .setResponseCode(200);
        mockServer.enqueue(mockResponse);
        try {
            mockServer.start();
        } catch (IOException e) {
            System.out.println("can't start mockServer");
        }
        serverBaseUrl = mockServer.url("/").toString();
        mockHost = (serverBaseUrl.split("/")[2]).split(":")[0];
        mockPort = Integer.parseInt((serverBaseUrl.split("/")[2]).split(":")[1]);
    }

    @AfterAll
    public static void afterAll() {
        try {
            mockServer.shutdown();
        } catch (IOException e) {

        }
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

    @Test
    void testCheckUrl() {
        HttpResponse<String> response1 = Unirest.post("/urls")
            .field("url", serverBaseUrl)
            .asString();

        Url url = new QUrl()
            .host.equalTo(mockHost)
            .port.equalTo(mockPort)
            .findOne();

        HttpResponse<String> response2 = Unirest.post("/urls/{id}/checks")
            .routeParam("id", url.getId().toString())
            .asString();

        UrlCheck urlCheck = url.getUrlChecks().get(url.getUrlChecks().size() - 1);

        HttpResponse<String> response3 = Unirest.get("/urls/{id}")
            .routeParam("id", url.getId().toString())
            .asString();
        String body = response3.getBody();

        assertThat(body).contains(urlCheck.getId().toString());
        assertThat(body).contains(urlCheck.getTitle().length() <= 30
            ? urlCheck.getTitle()
            : urlCheck.getTitle().substring(0, 9));
        assertThat(body).contains(urlCheck.getH1().length() <= 30
            ? urlCheck.getH1()
            : urlCheck.getH1().substring(0, 9));
        assertThat(body).contains(urlCheck.getDescription().length() <= 30
            ? urlCheck.getDescription()
            : urlCheck.getDescription().substring(0, 9));
    }
}
