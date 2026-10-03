package hexlet.code.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Страницы 404 и 500 в оформлении сайта вместо трассировки. */
public final class ErrorsController {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorsController.class);

    private ErrorsController() {}

    public static void notFound(Context ctx) {
        ctx.render("errors/404.jte");
    }

    public static void serverError(Exception exception, Context ctx) {
        // Сюда попадают и HttpResponseException (NotFoundResponse и т.п.) — у них свой код
        if (exception instanceof HttpResponseException response) {
            ctx.status(response.getStatus());
            if (response.getStatus() == HttpStatus.NOT_FOUND.getCode()) {
                notFound(ctx);
            } else {
                ctx.result(response.getMessage());
            }
            return;
        }
        LOG.error("Request {} {} failed", ctx.method(), ctx.path(), exception);
        ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        ctx.render("errors/500.jte");
    }
}
