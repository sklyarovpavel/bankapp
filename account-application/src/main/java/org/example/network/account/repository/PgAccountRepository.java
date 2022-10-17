package org.example.network.account.repository;

import lombok.RequiredArgsConstructor;
import org.example.network.account.api.repository.AccountRepository;
import org.example.network.account.domain.entity.AccountEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;

@RequiredArgsConstructor
public class PgAccountRepository implements AccountRepository {

    public static final String SELECT_FROM_ACCOUNT_WHERE_ACCOUNT_ID = "select * from account where id=:id";
    public static final String UPDATE_ACCOUNT = "update account set amount=:amount, version=:version+1 where version=:version and id=:id";
    public static final String INSERT_ACCOUNT = "insert into account(id, amount, currency, version)" +
            " values (:id, :amount, :currency, :version)";

    private final NamedParameterJdbcTemplate accountJdbcTemplate;

    @Override
    public AccountEntity getAccount(String accountId) {
        return accountJdbcTemplate.queryForObject(SELECT_FROM_ACCOUNT_WHERE_ACCOUNT_ID,
                Collections.singletonMap("id", accountId), new BeanPropertyRowMapper<>(AccountEntity.class));
    }

    @Override
    public boolean updateAccount(AccountEntity account) {
        return accountJdbcTemplate.update(UPDATE_ACCOUNT, new BeanPropertySqlParameterSource(account)) == 1;
    }

    @Override
    public void save(AccountEntity account) {
        accountJdbcTemplate.update(INSERT_ACCOUNT, new BeanPropertySqlParameterSource(account));
    }

}
