package hexlet.code.controllers;

import io.javalin.http.Handler;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.List;
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import org.jsoup.nodes.Document;
import java.util.Optional;
import org.jsoup.nodes.Element;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.model.query.QUrl;
import hexlet.code.App;

public class UrlController {

    public static Handler newUrl = ctx -> {
        ctx.render("index.html");
    };

    public static Handler createUrl = ctx -> {
        URL inputURL;
        String input = ctx.formParam("url");
        try {
            inputURL = new URL(input);
        } catch (MalformedURLException e) {
            ctx.status(400);
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.attribute("url", input);
            ctx.render("index.html");
            return;
        }
        String protocol = inputURL.getProtocol();
        String host = inputURL.getHost();
        String port = inputURL.getPort() == -1 ? "" : ":" + inputURL.getPort();
        Url newUrl = new Url(protocol + "://" + host + port);
        boolean urlExist = new QUrl()
            .name.equalTo(newUrl.getName())
            .exists();

        if (urlExist) {
            ctx.status(409);
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "warning");
            ctx.attribute("url", input);
            ctx.render("index.html");
            return;

        }
        newUrl.save();
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls");
    };

    public static Handler listUrl = ctx -> {
        List<Url> listUrl = new QUrl().findList();
        ctx.status(200);
        ctx.attribute("listUrl", listUrl);
        ctx.render("urls/index.html");
    };

    public static Handler showUrl = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        Url url = new QUrl().id.equalTo(id).findOne();
        if (url == null) {
            ctx.sessionAttribute("flash", "URL не найден");
            ctx.sessionAttribute("flash-type", "danger");
        }
        ctx.status(200);
        ctx.attribute("url", url);
        ctx.attribute("urlChecks", url.getUrlChecks());
        ctx.render("urls/show.html");
    };

    public static Handler checkUrl = ctx -> {
        HttpResponse<String> response;
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        Url url = new QUrl()
            .id.equalTo(id)
            .findOne();
        App.LOGGER.warn("path:" + url.getName());
        try {
            response = Unirest.get(url.getName())
            .asString();
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Сайт не существует");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/urls/" + id);
            return;
        }
        Document doc = Jsoup.parse(response.getBody());
        App.LOGGER.warn("body:" + doc.body().toString().substring(0, 20));
        String title = doc.title();
        App.LOGGER.warn("title:" + title);
        String h1 = Optional
            .<Element>ofNullable(doc.selectFirst("h1"))
            .map(value -> value.text())
            .orElse("");
        App.LOGGER.warn("h1:" + h1);
        String description = Optional
            .<Element>ofNullable(doc.selectFirst("meta[name=description][content]"))
            .map(value -> value
                .getElementsByAttribute("content")
                .first()
                .text())
            .orElse("");
        App.LOGGER.warn("description:" + description);
        UrlCheck urlCheck = new UrlCheck(response.getStatus(),
                                         title,
                                         h1,
                                         description,
                                         url);
        urlCheck.save();
        App.LOGGER.warn("urlChecl_ID:" + urlCheck.getId());
        ctx.sessionAttribute("flash", "Сайт успешно проверен");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls/" + id);
    };
}
