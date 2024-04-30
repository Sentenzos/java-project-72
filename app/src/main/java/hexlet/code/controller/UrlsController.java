package hexlet.code.controller;

import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import hexlet.code.util.NormalizeUrl;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.Collections;

public class UrlsController {
    public static void index(Context ctx) throws SQLException {
        String flash = ctx.consumeSessionAttribute("flash");
        String flashType = ctx.consumeSessionAttribute("flash-type");
        var urls = UrlRepository.getEntities();
        var checks = UrlCheckRepository.getLatestChecks();
        var page = new UrlsPage(urls, checks);
        page.setFlash(flash);
        page.setFlashType(flashType);
        ctx.render("urls/index.jte", Collections.singletonMap("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse(String.format("Url with id = %s not found", id)));
        var checks = UrlCheckRepository.findByUrlId(url.getId());
        String flash = ctx.consumeSessionAttribute("flash");
        String flashType = ctx.consumeSessionAttribute("flash-type");
        var page = new UrlPage(url);
        page.setChecks(checks);
        page.setFlash(flash);
        page.setFlashType(flashType);
        ctx.render("urls/show.jte", Collections.singletonMap("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        var url = ctx.formParamAsClass("url", String.class).get();
        try {
            String uri = NormalizeUrl.normalize(url);
            if (UrlRepository.findByName(uri).isPresent()) {
                throw new SQLDataException();
            }
            UrlRepository.save(new Url(uri));
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flash-type", "alert-success");
            ctx.status(302);
            ctx.redirect(NamedRoutes.urlsPath());
        } catch (MalformedURLException | URISyntaxException | IllegalArgumentException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flashType", "alert-danger");
            ctx.status(422);
            ctx.redirect(NamedRoutes.rootPath());
        } catch (SQLDataException e) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "alert-danger");
            ctx.status(422);
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }

    public static void check(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id).get();
        var response = Unirest.get(url.getName()).asString();
        var res = Jsoup.parse(response.getBody());
        Element h1El = res.selectFirst("h1");
        Element descriptionEl = res.selectFirst("meta[name=\"description\"]");
        String title = res.title();
        String h1 = h1El == null ? "" : h1El.text();
        String description = descriptionEl == null ? "" : descriptionEl.attr("content");
        Integer status = response.getStatus();

        var check = new UrlCheck();
        check.setUrlId(id);
        check.setStatusCode(status);
        check.setTitle(title);
        check.setH1(h1);
        check.setDescription(description);
        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.sessionAttribute("flash-type", "alert-success");

        UrlCheckRepository.save(check);

        ctx.status(302);
        ctx.redirect(NamedRoutes.urlPath(id));
    }
}
