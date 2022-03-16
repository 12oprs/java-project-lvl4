package hexlet.code;

import io.javalin.Javalin;

public class App {

    private static Javalin app;

    public static Javalin getApp() {
        Javalin app = Javalin.create();
        app.create(config -> config.enableDevLogging());
        return app;
    }

    public static void main (String[] args) {
        Javalin app = getApp();
        app.start(5000);
        app.get("/", ctx -> ctx.result("Hello, world!"));
    }

}