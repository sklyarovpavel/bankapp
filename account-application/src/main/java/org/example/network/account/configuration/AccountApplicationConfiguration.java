package org.example.network.account.configuration;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import jakarta.transaction.TransactionSynchronizationRegistry;
import org.example.network.account.api.repository.RoutingRepository;
import org.example.network.account.api.service.account.AccountService;
import org.example.network.account.api.service.conversion.ConversionServce;
import org.example.network.account.api.service.report.ReportServiceClient;
import org.example.network.account.api.service.rounting.RepositoryRoutingService;
import org.example.network.account.repository.PgAccountRepository;
import org.example.network.account.repository.PgRoutingRepository;
import org.example.network.account.service.account.DefaultAccountService;
import org.example.network.account.service.account.RetryingAccountService;
import org.example.network.account.service.conversion.StubConversionServce;
import org.example.network.account.service.report.RestReportServiceClient;
import org.example.network.account.service.routing.DefaultRoutingService;
import org.postgresql.xa.PGXADataSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.transaction.SystemException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(AccountApplicationProperties.class)
public class AccountApplicationConfiguration {

    @Bean
    public RepositoryRoutingService repositoryRoutingService(RoutingRepository routingRepository,
                                                             AccountApplicationProperties properties,
                                                             Environment environment) {
        Map<String, PgAccountRepository> accountRepoMap = properties.getDatasources()
                .entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals("routing"))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        this::getPgAccountRepository));
        List<String> shardList = new ArrayList<>(new TreeSet<>(accountRepoMap.keySet()));
        return new DefaultRoutingService(routingRepository, shardList, accountRepoMap, properties);
    }

    private PgAccountRepository getPgAccountRepository(Map.Entry<String, DatasourceConfig> entry) {
        TransactionSynchronizationRegistry tt = null;
        DatasourceConfig value = entry.getValue();
        PGXADataSource pgxaDataSource = new PGXADataSource();
        pgxaDataSource.setUrl(value.getUrl());
        pgxaDataSource.setUser(value.getUser());
        pgxaDataSource.setPassword(value.getPassword());
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setXaDataSource(pgxaDataSource);
        return new PgAccountRepository(new NamedParameterJdbcTemplate(atomikosDataSourceBean));
    }

    @Bean
    public ReportServiceClient reportServiceClient() {
        return new RestReportServiceClient();

    }
    @Bean
    public ConversionServce conversionServce() {
        return new StubConversionServce();
    }

    @Bean
    public AccountService defaultAccountService(AccountApplicationProperties properties,
                                                RepositoryRoutingService routingService,
                                                TransactionTemplate transactionTemplate,
                                                ConversionServce conversionServce,
                                                ReportServiceClient reportService) {
        return new RetryingAccountService(properties,
                new DefaultAccountService(routingService, transactionTemplate, conversionServce, reportService));
    }

    @Bean
    public RoutingRepository routingRepository(AccountApplicationProperties properties) {
        DatasourceConfig routing = properties.getDatasources().get("routing");
        PGXADataSource routingDataSource = new PGXADataSource();
        routingDataSource.setUrl(routing.getUrl());
        routingDataSource.setUser(routing.getUser());
        routingDataSource.setPassword(routing.getPassword());
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setXaDataSource(routingDataSource);
        return new PgRoutingRepository(new JdbcTemplate(atomikosDataSourceBean));
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager jtaTransactionManager) {
        return new TransactionTemplate(jtaTransactionManager);
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public UserTransactionManager userTransactionManager() throws SystemException {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setTransactionTimeout(300);
        userTransactionManager.setForceShutdown(true);
        return userTransactionManager;
    }

    @Bean
    public JtaTransactionManager jtaTransactionManager() throws SystemException {
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
        jtaTransactionManager.setTransactionManager(userTransactionManager());
        jtaTransactionManager.setUserTransaction(userTransactionManager());
        return jtaTransactionManager;
    }

}
