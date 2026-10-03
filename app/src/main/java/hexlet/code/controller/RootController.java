package hexlet.code.controller;

import hexlet.code.dto.MainPage;
import io.javalin.http.Context;
import java.util.Map;

public final class RootController {

    private RootController() {}

    public static void index(Context ctx) {
        var page = new MainPage("");
        Flash.moveTo(ctx, page);
        ctx.render("index.jte", Map.of("page", page));
    }
}
