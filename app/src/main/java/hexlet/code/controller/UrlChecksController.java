package hexlet.code.controller;

import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.service.UrlCheckException;
import hexlet.code.service.UrlChecker;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.sql.SQLException;

public final class UrlChecksController {

    private UrlChecksController() {}

    public static void create(Context ctx) throws SQLException {
        var id = UrlsController.parseId(ctx.pathParam("id"));
        var url =
                UrlRepository.find(id)
                        .orElseThrow(
                                () -> new NotFoundResponse("Url with id " + id + " not found"));
        try {
            UrlCheckRepository.save(UrlChecker.check(url));
            Flash.put(ctx, "Страница успешно проверена", Flash.SUCCESS);
        } catch (UrlCheckException e) {
            Flash.put(ctx, "Произошла ошибка при проверке", Flash.DANGER);
        }
        ctx.redirect(NamedRoutes.urlPath(id));
    }
}
