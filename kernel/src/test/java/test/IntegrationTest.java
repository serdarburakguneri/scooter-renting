package test;

import io.quarkus.test.hibernate.reactive.panache.TransactionalUniAsserter;
import io.quarkus.test.vertx.RunOnVertxContext;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;


@Tag("IntegrationTest")
public abstract class IntegrationTest {

    @Inject
    DatabaseHelper db;

    public DatabaseHelper db() {
        return db;
    }

    @AfterEach
    @RunOnVertxContext
    void cleanupDatabase(TransactionalUniAsserter asserter) {
        asserter.execute(() -> db().cleanDatabase());
    }
}
