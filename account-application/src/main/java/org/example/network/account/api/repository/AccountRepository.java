package org.example.network.account.api.repository;

import org.example.network.account.domain.entity.AccountEntity;

public interface AccountRepository {

    AccountEntity getAccount(String accountId);

    boolean updateAccount(AccountEntity account);

    void save(AccountEntity account);
}
