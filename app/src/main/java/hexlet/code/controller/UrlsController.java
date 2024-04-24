package hexlet.code.controller;

import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import hexlet.code.util.NormalizeUrl;
import io.javalin.http.Context;

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
        var urls = UrlsRepository.getEntities();
        var page = new UrlsPage(urls, flash);
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        String flash = ctx.consumeSessionAttribute("flash");
        //TODO рефактор
        var urlOptional = UrlsRepository.find(id);
        if (urlOptional.isPresent()) {
            var url = urlOptional.get();
            var page = new UrlPage(url);
            ctx.render("urls/show.jte", model("page", page));
        } else {
            ctx.result("Page not found").status(404);
        }
    }

    public static void create(Context ctx) throws SQLException {
        var url = ctx.formParamAsClass("url", String.class).get();
        try {
            String uri = NormalizeUrl.normalize(url);
            if (UrlsRepository.findByName(uri).isPresent()) {
                throw new SQLDataException();
            }
            var createdAt = new Timestamp(System.currentTimeMillis());
            UrlsRepository.save(new Url(uri, createdAt));
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
}
