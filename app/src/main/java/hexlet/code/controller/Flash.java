package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import io.javalin.http.Context;

/** Flash-сообщение переживает редирект в сессии и показывается один раз. */
final class Flash {

    private static final String MESSAGE = "flash";
    private static final String TYPE = "flash-type";

    static final String SUCCESS = "success";
    static final String DANGER = "danger";

    private Flash() {}

    static void put(Context ctx, String message, String type) {
        ctx.sessionAttribute(MESSAGE, message);
        ctx.sessionAttribute(TYPE, type);
    }

    static void moveTo(Context ctx, BasePage page) {
        String message = ctx.consumeSessionAttribute(MESSAGE);
        String type = ctx.consumeSessionAttribute(TYPE);
        if (message != null) {
            page.setFlash(message, type);
        }
    }
}
