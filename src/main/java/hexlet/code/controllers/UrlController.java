package hexlet.code.controllers;

import io.javalin.http.Handler;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.List;

import hexlet.code.model.Url;
import hexlet.code.model.query.QUrl;

import hexlet.code.App;

public class UrlController {

    public static Handler newUrl = ctx -> {
        ctx.render("index.html");
    };

    public static Handler createUrl = ctx -> {
        URL inputURL = null;
        String input = ctx.formParam("url");
        try {
            inputURL = new URL(input);
        } catch (MalformedURLException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.attribute("url", input);
            ctx.render("index.html");
            return;
        }
        String protocol = inputURL.getProtocol();
        String host = inputURL.getHost();
        Integer port = inputURL.getPort() == -1 ? null : inputURL.getPort();
        Url newUrl = new Url(protocol, host, port);
        boolean urlExist = new QUrl()
            .protocol.equalTo(newUrl.getProtocol())
            .host.equalTo(newUrl.getHost())
            .port.equalTo(newUrl.getPort())
            .exists();

        if (urlExist) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "warning");
            ctx.attribute("url", input);
            ctx.render("index.html");
            return;

        }

        App.LOGGER.warn("before save");
        newUrl.save();
        App.LOGGER.warn("after save");

        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        App.LOGGER.warn("before redirect to urls");
        ctx.redirect("/urls");
    };

    public static Handler listUrl = ctx -> {
        App.LOGGER.warn("start listUrl");

        List<Url> listUrl = new QUrl().findList();

        ctx.attribute("listUrl", listUrl);
        ctx.render("urls/index.html");
    };

    public static Handler showUrl = ctx -> {
        String[] temp = ctx.path().split("/");
        long id = Long.parseLong(temp[2]);
        Url url = new QUrl().id.equalTo(id).findOne();
        if (url == null) {
            ctx.sessionAttribute("flash", "URL не найден");
            ctx.sessionAttribute("flash-type", "danger");
        }
        ctx.attribute("url", url);
        ctx.render("urls/show.html");
    };

}
