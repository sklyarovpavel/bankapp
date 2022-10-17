package org.example.network.account.api.repository;

import org.example.network.account.domain.entity.AccountRouting;

public interface RoutingRepository {

    String getShardNameByAccountId(String accountNumber);

    long getNextSequenceValue();

    void save(AccountRouting Routing);

}
