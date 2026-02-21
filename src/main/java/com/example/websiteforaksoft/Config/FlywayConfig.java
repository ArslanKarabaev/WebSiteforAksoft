package com.example.websiteforaksoft.Config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Bean
    CommandLineRunner runFlyway(DataSource dataSource) {
        return args -> {
            System.out.println("===== MANUALLY RUNNING FLYWAY =====");

            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .load();

            System.out.println("Flyway configured with locations: classpath:db/migration");

            MigrateResult result = flyway.migrate();

            System.out.println("===== FLYWAY MIGRATION RESULT =====");
            System.out.println("Migrations executed: " + result.migrationsExecuted);
            System.out.println("Success: " + result.success);
            System.out.println("===== FLYWAY COMPLETED =====");
        };
    }
}