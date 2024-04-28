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
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Timestamp;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {
    public static void index(Context ctx) throws SQLException {
        String flash = ctx.consumeSessionAttribute("flash");
        //TODO обработка кейса, когда нет найденных
        var urls = UrlRepository.getEntities();
        var page = new UrlsPage(urls, flash);
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();

        //TODO рефактор
        var urlOptional = UrlRepository.find(id);
        if (urlOptional.isPresent()) {
            var url = urlOptional.get();
            var checks = UrlCheckRepository.findByUrlId(url.getId());
            String flash = ctx.consumeSessionAttribute("flash");
            var page = new UrlPage(url);
            page.setChecks(checks);
            page.setFlash(flash);
            ctx.render("urls/show.jte", model("page", page));
        } else {
            ctx.result("Page not found").status(404);
        }
    }

    public static void create(Context ctx) throws SQLException {
        var url = ctx.formParamAsClass("url", String.class).get();
        try {
            String uri = NormalizeUrl.normalize(url);
            if (UrlRepository.findByName(uri).isPresent()) {
                throw new SQLDataException();
            }
            var createdAt = new Timestamp(System.currentTimeMillis());
            UrlRepository.save(new Url(uri, createdAt));
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.status(302);
            ctx.redirect(NamedRoutes.urlsPath());
        } catch (MalformedURLException | URISyntaxException | IllegalArgumentException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.status(422);
            ctx.redirect(NamedRoutes.rootPath());
        } catch (SQLDataException e) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.status(422);
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }

    public static void check(Context ctx) throws SQLException, IOException {
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
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        var check = new UrlCheck();
        check.setUrlId(id);
        check.setStatusCode(status);
        check.setCreatedAt(createdAt);
        check.setTitle(title);
        check.setH1(h1);
        check.setDescription(description);
        ctx.sessionAttribute("flash", "Страница успешно проверена");
        UrlCheckRepository.save(check);

        ctx.status(302);
        ctx.redirect(NamedRoutes.urlPath(id));
    }
}
