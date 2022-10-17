package org.example.network.account.service.routing;

import lombok.RequiredArgsConstructor;
import org.example.network.account.api.repository.AccountRepository;
import org.example.network.account.api.service.rounting.RepositoryRoutingService;
import org.example.network.account.api.repository.RoutingRepository;
import org.example.network.account.configuration.AccountApplicationProperties;
import org.example.network.account.domain.entity.AccountRouting;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class DefaultRoutingService implements RepositoryRoutingService {

    private final RoutingRepository routingRepository;

    private final List<String> shardList;

    private final Map<String, ? extends AccountRepository> repositoryMap;

    private final AccountApplicationProperties accountApplicationProperties;

    @Override
    public AccountRepository getRepositoryNameByAccountId(String accountNumber) {
        String shardName = routingRepository.getShardNameByAccountId(accountNumber);
        return repositoryMap.get(shardName);
    }

    @Override
    public void saveRouting(String accountId) {
        long nextSequenceValue = routingRepository.getNextSequenceValue();
        int shardIndex = (int) (nextSequenceValue / accountApplicationProperties.getMaxAccountsPerShard());
        AccountRouting routing = new AccountRouting();
        routing.setAccountId(accountId);
        routing.setId(nextSequenceValue);
        routing.setShardId(shardList.get(shardIndex));
        routingRepository.save(routing);
    }

}
