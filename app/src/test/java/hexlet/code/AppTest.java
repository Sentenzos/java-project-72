package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NormalizeUrl;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppTest {

    private static Javalin app;

    @BeforeEach
    public void setUp() throws Exception {
        app = App.getApp();
    }

    @Test
    void testMainPageStatus() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testNormalizeUrl() throws Exception {
        var actualUrl = "https://ru.hexlet.io/projects/72/members/38737?step=6";
        String expectedUrl = "https://ru.hexlet.io";

        assertThat(NormalizeUrl.normalize(actualUrl)).isEqualTo(expectedUrl);
        assertThrows(Exception.class, () -> NormalizeUrl.normalize("sadsada"));
    }

    @Test
    void testUrlPage() throws Exception {
        var createdAt = new Timestamp(System.currentTimeMillis());
        String expectedUrl = "https://ru.hexlet.io";
        var url = new Url(expectedUrl, createdAt);
        UrlsRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains(expectedUrl);
        });
    }

    @Test
    void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testUrlNotFound() {
        JavalinTest.test(app, ((server, client) -> {
            var response = client.get("/urls/999999");
            assertThat(response.code()).isEqualTo(404);
        }));
    }
}
