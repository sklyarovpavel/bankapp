package org.example.network.account.repository;

import lombok.RequiredArgsConstructor;
import org.example.network.account.api.repository.RoutingRepository;
import org.example.network.account.domain.entity.AccountRouting;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
public class PgRoutingRepository implements RoutingRepository {

    public static final String GET_SHARD_ID = "select shard_id from routing where account_id=?";

    private final JdbcTemplate routingJdbcTemplate;

    @Override
    public String getShardNameByAccountId(String accountid) {
        return routingJdbcTemplate.queryForObject(GET_SHARD_ID, String.class, accountid);
    }

    @Override
    public long getNextSequenceValue() {
        return routingJdbcTemplate.queryForObject("SELECT nextval('routing_sequesnce')", Long.class);
    }

    @Override
    public void save(AccountRouting Routing) {
        routingJdbcTemplate.update("insert into routing(id, account_id, shard_id) values (?, ?, ?)",
                Routing.getId(), Routing.getAccountId(), Routing.getShardId());
    }

}
