package org.example.network.account.repository;

import org.example.network.account.domain.entity.AccountEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import ru.yandex.qatools.embed.postgresql.distribution.Version;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultAccountRepositoryTest {

    private static final EmbeddedPostgres postgres = new EmbeddedPostgres(Version.Main.V11);;
    private static PgAccountRepository repository;

    @BeforeAll
    static void init() throws Exception {
        EmbeddedPostgres postgres = new EmbeddedPostgres(Version.Main.V11);
        String url = postgres.start("localhost", 5433, "dbName", "userName", "password");
        DataSource ds = new DriverManagerDataSource(url);
        ScriptUtils.executeSqlScript(ds.getConnection(), new ClassPathResource("create_account_database.sql"));
        repository = new PgAccountRepository(new NamedParameterJdbcTemplate(ds));
    }

    @AfterAll
    static void stop() throws Exception {
        try {
            postgres.stop();
        } catch (Exception ex) {

        }
    }
    @Test
    public void testSaveGetAccountReturnsSame() {
        String id = UUID.randomUUID().toString();
        AccountEntity account  = new AccountEntity();
        account.setId(id);
        account.setAmount(BigDecimal.ONE);
        account.setCurrency("USD");
        repository.save(account);
        AccountEntity fromDatabase = repository.getAccount(id);
        assertEquals(account, fromDatabase);
    }

    @Test
    public  void testUpatetReturnsChangedAccount() {
        String id = UUID.randomUUID().toString();
        AccountEntity account  = new AccountEntity();
        account.setId(id);
        account.setAmount(BigDecimal.ONE);
        account.setCurrency("USD");
        repository.save(account);
        account.setAmount(BigDecimal.TEN);
        repository.updateAccount(account);
        AccountEntity fromDatabase = repository.getAccount(id);
        assertEquals(account.getId(), fromDatabase.getId());
        assertEquals(account.getAmount(), fromDatabase.getAmount());
        assertEquals(account.getCurrency(), fromDatabase.getCurrency());
        assertEquals(account.getVersion() + 1, fromDatabase.getVersion());
    }

}