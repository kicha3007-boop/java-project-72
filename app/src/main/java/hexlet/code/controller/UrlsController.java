package hexlet.code.controller;

import hexlet.code.dto.MainPage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import hexlet.code.util.UrlNormalizer;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import java.sql.SQLException;
import java.util.Map;

public final class UrlsController {

    private UrlsController() {}

    public static void index(Context ctx) throws SQLException {
        var page = new UrlsPage(UrlRepository.getEntities(), UrlCheckRepository.findLatestChecks());
        Flash.moveTo(ctx, page);
        ctx.render("urls/index.jte", Map.of("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = parseId(ctx.pathParam("id"));
        var url =
                UrlRepository.find(id)
                        .orElseThrow(
                                () -> new NotFoundResponse("Url with id " + id + " not found"));
        var page = new UrlPage(url, UrlCheckRepository.findByUrlId(id));
        Flash.moveTo(ctx, page);
        ctx.render("urls/show.jte", Map.of("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        var input = ctx.formParam("url");
        var name = UrlNormalizer.normalize(input);
        if (name.isEmpty()) {
            var page = new MainPage(input);
            page.setFlash("Некорректный URL", Flash.DANGER);
            ctx.status(HttpStatus.UNPROCESSABLE_CONTENT);
            ctx.render("index.jte", Map.of("page", page));
            return;
        }

        var existing = UrlRepository.findByName(name.get());
        if (existing.isPresent()) {
            Flash.put(ctx, "Страница уже существует", Flash.DANGER);
            ctx.redirect(NamedRoutes.urlPath(existing.get().getId()));
            return;
        }

        var url = new Url(name.get());
        UrlRepository.save(url);
        Flash.put(ctx, "Страница успешно добавлена", Flash.SUCCESS);
        ctx.redirect(NamedRoutes.urlPath(url.getId()));
    }

    static long parseId(String raw) {
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            throw new NotFoundResponse("Url with id " + raw + " not found");
        }
    }
}
