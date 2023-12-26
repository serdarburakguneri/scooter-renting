package startup.dbmigration;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;

/*
 Not something I like, but with reactive db connections, we can't run flyway migrations automatically.
 */
@ApplicationScoped
public class DatabaseMigrator {

    @ConfigProperty(name = "quarkus.datasource.reactive.url")
    String datasourceUrl;
    @ConfigProperty(name = "quarkus.datasource.username")
    String datasourceUsername;
    @ConfigProperty(name = "quarkus.datasource.password")
    String datasourcePassword;

    public void runFlywayMigration(@Observes StartupEvent event) {

        var dataSourceUrl = "jdbc:" + datasourceUrl.substring("vertx-reactive:".length());

        var flyway =
                Flyway.configure().dataSource(dataSourceUrl, datasourceUsername, datasourcePassword).load();
        flyway.migrate();
    }
}
