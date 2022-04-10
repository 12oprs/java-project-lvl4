package hexlet.code.model;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Lob;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.DbDefault;
import java.time.Instant;
import io.ebean.Model;

@Entity
public class UrlCheck extends Model {

    @Id
    private long id;

    @DbDefault("0")
    private int statusCode;

    private String title;

    private String h1;

    @Lob
    private String description;

    @ManyToOne(optional = false)
    private Url url;

    @WhenCreated
    private Instant createdAt;

    public UrlCheck(int newStatusCode,
                    String newTitle,
                    String newH1,
                    String newDescription,
                    Url newUrl) {
        this.statusCode = newStatusCode;
        this.title = newTitle;
        this.h1 = newH1;
        this.description = newDescription;
        this.url = newUrl;
    }

    public final Long getId() {
        return id;
    }

    public final int getStatusCode() {
        return statusCode;
    }

    public final String getTitle() {
        return title;
    }

    public final String getH1() {
        return h1;
    }

    public final String getDescription() {
        return description;
    }

    public final Url getUrl() {
        return url;
    }

    public final Instant getCreatedAt() {
        return createdAt;
    }


}
