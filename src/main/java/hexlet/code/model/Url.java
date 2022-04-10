package hexlet.code.model;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import io.ebean.annotation.WhenCreated;
import javax.persistence.CascadeType;

import java.util.ArrayList;
import java.util.List;
import java.time.Instant;
import io.ebean.Model;

@Entity
public class Url extends Model {

    @Id
    private long id;

    private String name;

    @WhenCreated
    private Instant createdAt;

    @OneToMany(cascade = CascadeType.ALL)
    private List<UrlCheck> urlChecks = new ArrayList<>();

    public Url(String newName) {
        this.name = newName;
    }

    public final Long getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public final Instant getCreatedAt() {
        return createdAt;
    }

    public final List<UrlCheck> getUrlChecks() {
        return urlChecks;
    }
}
