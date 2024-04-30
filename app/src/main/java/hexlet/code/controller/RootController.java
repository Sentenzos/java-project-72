package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import io.javalin.http.Context;

import java.util.Collections;

public class RootController {
    public static void index(Context ctx) {
        String flash = ctx.consumeSessionAttribute("flash");
        String flashType = ctx.consumeSessionAttribute("flashType");
        var page = new BasePage();
        page.setFlash(flash);
        page.setFlashType(flashType);
        ctx.render("index.jte", Collections.singletonMap("page", page));
    }
}
