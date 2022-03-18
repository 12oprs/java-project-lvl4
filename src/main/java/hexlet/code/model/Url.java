package hexlet.code.model;

import javax.persistence.Id;
import javax.persistence.Entity;
import io.ebean.annotation.WhenCreated;
import java.time.Instant;
import io.ebean.Model;

@Entity
public class Url extends Model {

    @Id
    private long id;

    private String name;

    @WhenCreated
    private Instant createdAt;

    public final long getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public final Instant getCreatedAt() {
        return createdAt;
    }
}
