package com.example.utilities;
import org.flywaydb.core.Flyway;

public class DbMigration {
    public static void migrate(String url, String user, String password) {
        Flyway flyway = Flyway.configure()
                .dataSource(url, user, password)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();
        flyway.migrate();
    }
}
