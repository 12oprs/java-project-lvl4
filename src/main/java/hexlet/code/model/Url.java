package hexlet.code.model;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import io.ebean.annotation.WhenCreated;
//import io.ebean.annotation.DbDefault;
import java.time.Instant;
import io.ebean.Model;

@Entity
@Table(name = "urls")
public class Url extends Model {

    @Id
    private long id;

    private String protocol;

    private String host;

    //@DbDefault("433")
    private Integer port;

    @WhenCreated
    private Instant createdAt;

    public Url(String newProtocol, String newHost, Integer newPort) {
        this.protocol = newProtocol;
        this.host = newHost;
        this.port = newPort;
    }

    public final Long getId() {
        return id;
    }

    public final String getProtocol() {
        return protocol;
    }

    public final String getHost() {
        return host;
    }

    public final Integer getPort() {
        return port;
    }

    public final Instant getCreatedAt() {
        return createdAt;
    }
}
