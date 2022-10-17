package org.example.network.account.api.service.rounting;

import org.example.network.account.api.repository.AccountRepository;
import org.example.network.account.domain.entity.AccountRouting;

public interface RepositoryRoutingService {

    AccountRepository getRepositoryNameByAccountId(String accountId);

    void saveRouting(String accountId);

}
