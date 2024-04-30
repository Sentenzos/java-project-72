package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NormalizeUrl;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {

    private static Javalin app;
    private static MockWebServer mockServer;

    private static Path getFixturePath() {
        return Paths.get("src", "test", "resources", "fixtures", "index.html")
                .toAbsolutePath().normalize();
    }

    private static String getFixture() throws IOException {
        Path filePath = getFixturePath();
        return Files.readString(filePath).trim();
    }

    @BeforeEach
    public void setUp() throws Exception {
        app = App.getApp();
    }

    @BeforeAll
    public static void startMockServer() throws IOException {
        mockServer = new MockWebServer();
        MockResponse mockResponse = new MockResponse()
                .setBody(getFixture());
        mockServer.enqueue(mockResponse);
    }

    @AfterAll
    public static void stopMockServer() throws IOException {
        mockServer.shutdown();
    }

    @Test
    void testMainPageStatus() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testUrlPage() throws Exception {
        var createdAt = new Timestamp(System.currentTimeMillis());
        String expectedUrl = "https://ru.hexlet.io";
        var url = new Url(expectedUrl);
        url.setCreatedAt(createdAt);
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains(expectedUrl);
        });
    }

    @Test
    void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testCreateUrl() {
        JavalinTest.test(app, ((server, client) -> {
            String requestBody = "url=https://java-page-analyzer-ru.hexlet.app/dsfsdfsfs/aaaa";
            Response response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string())
                    .contains("<a href=\"/urls/1\">https://java-page-analyzer-ru.hexlet.app</a>");
            assertThat(UrlRepository.findByName("https://java-page-analyzer-ru.hexlet.app").isPresent()).isTrue();
        }));
    }

    @Test
    void testCheck() {
        String mockUrl = mockServer.url("/").toString();
        JavalinTest.test(app, ((server, client) -> {
            String requestBody = "url=" + mockUrl;
            assertThat(client.post("/urls", requestBody).code())
                    .isEqualTo(200);

            String name = NormalizeUrl.normalize(mockServer.url("/").toString());
            Url url = UrlRepository.findByName(name).orElse(null);
            assertThat(url).isNotNull();

            assertThat(client.post("/urls/" + url.getId() + "/checks").code())
                    .isEqualTo(200);

            UrlCheck urlCheck = UrlCheckRepository.findByUrlId(url.getId()).getFirst();
            assertThat(urlCheck.getTitle()).isEqualTo("Test title");
            assertThat(urlCheck.getDescription()).isEqualTo("Test description");
            assertThat(urlCheck.getH1()).isEqualTo("Hello, Hexlet!");
        }));
    }

    @Test
    public void testUrlNotFound() {
        JavalinTest.test(app, ((server, client) -> {
            var response = client.get("/urls/999999");
            assertThat(response.code()).isEqualTo(404);
        }));
    }
}
