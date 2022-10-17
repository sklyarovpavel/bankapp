package org.example.network.account.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(value = "account", ignoreUnknownFields = false)
public class AccountApplicationProperties {

    private Map<String, DatasourceConfig> datasources;

    private long operationTimeoutInMilliseconds;

    private int maxAccountsPerShard;

}
