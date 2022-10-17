package org.example.network.account.repository;

import org.example.network.account.domain.entity.AccountRouting;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import ru.yandex.qatools.embed.postgresql.distribution.Version;

import javax.sql.DataSource;

import java.util.UUID;

class PgRoutingRepositoryTest {

    static EmbeddedPostgres postgres;
    static PgRoutingRepository repository;

    @BeforeAll
    static void init() throws Exception {
        EmbeddedPostgres postgres = new EmbeddedPostgres(Version.Main.V11);
        String url = postgres.start("localhost", 5433, "dbName", "userName", "password");
        DataSource ds = new DriverManagerDataSource(url);
        ScriptUtils.executeSqlScript(ds.getConnection(), new ClassPathResource("create_routing_database.sql"));
        repository = new PgRoutingRepository(new JdbcTemplate(ds));
    }

    @AfterAll
    static void stop() throws Exception {
        try {
            postgres.stop();
        } catch (Exception ex) {
        }
    }

    @Test
    void testGetSequenceValueReturnsDifferentValues() {
        long first = repository.getNextSequenceValue();
        long second = repository.getNextSequenceValue();
        Assertions.assertNotEquals(first, second);
    }

    @Test
    void testGetReturnsSavedValue() {
        String accountId = UUID.randomUUID().toString();
        String shardId = "custom";
        AccountRouting routing = new AccountRouting();
        routing.setAccountId(accountId);
        routing.setShardId(shardId);
        routing.setId(repository.getNextSequenceValue());
        repository.save(routing);
        String shardNameByAccountId = repository.getShardNameByAccountId(accountId);
        Assertions.assertEquals(shardNameByAccountId, shardId);
    }

    @Test
    void testExceptionWhenRoutingAlreadyExists() {
        String accountId = UUID.randomUUID().toString();
        String shardId = "custom";
        AccountRouting first = new AccountRouting();
        first.setAccountId(accountId);
        first.setShardId(shardId);
        first.setId(repository.getNextSequenceValue());
        AccountRouting second = new AccountRouting();
        second.setAccountId(accountId);
        second.setShardId(shardId);
        second.setId(repository.getNextSequenceValue());
        Assertions.assertThrows(DuplicateKeyException.class, () -> repository.save(second));
    }
}