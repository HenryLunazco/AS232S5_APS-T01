package vg.edu.pe.HinoPE;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DatabaseConnectionTest {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private DatabaseClient databaseClient;

    @Test
    void testConnectionFactoryIsNotNull() {
        assertThat(connectionFactory).isNotNull();
    }

    @Test
    void testDatabaseClientIsNotNull() {
        assertThat(databaseClient).isNotNull();
    }

    @Test
    void testDatabaseConnection() {
        StepVerifier.create(
                databaseClient.sql("SELECT 1 as result")
                        .fetch()
                        .one()
        )
        .assertNext(result -> {
            assertThat(result).isNotNull();
            assertThat(result.get("result")).isEqualTo(1);
        })
        .verifyComplete();
    }

    @Test
    void testDatabaseVersion() {
        StepVerifier.create(
                databaseClient.sql("SELECT version()")
                        .fetch()
                        .one()
        )
        .assertNext(result -> {
            assertThat(result).isNotNull();
            assertThat(result.get("version")).isNotNull();
            System.out.println("PostgreSQL Version: " + result.get("version"));
        })
        .verifyComplete();
    }

    @Test
    void testTablesExist() {
        StepVerifier.create(
                databaseClient.sql(
                        "SELECT table_name FROM information_schema.tables " +
                        "WHERE table_schema = 'public' " +
                        "ORDER BY table_name"
                )
                .fetch()
                .all()
                .collectList()
        )
        .assertNext(tables -> {
            assertThat(tables).isNotEmpty();
            
            // Print all found tables
            System.out.println("Found tables:");
            tables.forEach(table -> {
                String tableName = (String) table.get("table_name");
                System.out.println("  - " + tableName);
            });
            
            // Verify expected tables exist
            java.util.List<String> tableNames = tables.stream()
                    .map(table -> (String) table.get("table_name"))
                    .toList();
            
            assertThat(tableNames).contains("notifications");
            // Add more table assertions as needed
        })
        .verifyComplete();
    }
}
