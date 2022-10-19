package org.example.network.account.report.repository;

import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.example.network.account.report.configuration.ReportApplicationConfigurationProperties;
import org.example.network.account.report.model.dto.ReportStatus;
import org.example.network.account.report.model.entity.ReportEntity;
import org.example.network.account.report.model.entity.TransferEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MongoReportRepositoryTest {

    private static final String CONNECTION_STRING = "mongodb://%s:%d";

    private MongodExecutable mongodExecutable;
    private MongoTemplate mongoTemplate;
    private MongoReportRepository mongoReportRepository;
    private ReportApplicationConfigurationProperties configuration = mock(ReportApplicationConfigurationProperties.class);

    @AfterEach
    void clean() {
        mongodExecutable.stop();
    }

    @BeforeEach
    void setup() throws Exception {
        String ip = "localhost";
        int port = 27017;

        ImmutableMongodConfig mongodConfig = MongodConfig
                .builder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(ip, port, Network.localhostIsIPv6()))
                .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();
        mongoTemplate = new MongoTemplate(MongoClients.create(String.format(CONNECTION_STRING, ip, port)), "test");
        mongoReportRepository = new MongoReportRepository(mongoTemplate, configuration);
    }


    @Test
    void findAllByAccountNumber() {
        mongoReportRepository.findAllByAccountId("test");
    }

    @Test
    void saveTransfer() {
        String accountId = getGuid();
        String id = getGuid();
        TransferEntity transferEntity = TransferEntity
                .builder().accountId(accountId).amount(BigDecimal.ONE).id(id).build();
        mongoReportRepository.saveTransfer(transferEntity);
        List<TransferEntity> allByAccountId = mongoReportRepository.findAllByAccountId(accountId);
        TransferEntity result = allByAccountId.stream().filter(entity -> entity.getId().equals(id))
                .findFirst().orElse(null);
        assertEquals(result, transferEntity);
    }

    @Test
    void getUnfinishedRequests() {
        ReportEntity report = createReport();
        List<ReportEntity> unfinishedRequests = mongoReportRepository.getUnfinishedRequests();
        ReportEntity result = unfinishedRequests
                .stream().filter(entity -> entity.getId().equals(report.getId())).findFirst().orElse(null);
        assertEquals(result, report);
    }

    private ReportEntity createReport() {
        ReportEntity report = ReportEntity.builder()
                .id(getGuid())
                .accountNumber(getGuid())
                .updateTime(new Date(0))
                .creationTime(new Date())
                .reportStatus(ReportStatus.NOT_YET_READY).build();
        mongoReportRepository.save(report);
        return report;
    }

    @Test
    void testBasicOperations() {
        ReportEntity report = createReport();
        when(configuration.getReportProcessingTimeoutInMinutes()).thenReturn(0);
        ReportEntity changed = mongoReportRepository.getReportAndChangeUpdateTime(report);
        ReportEntity result = mongoReportRepository.getReport(report.getId());
        assertEquals(changed, result);
    }

    @Test
    void findAndModifyConsistencyTest() {
        ReportEntity report = createReport();
        when(configuration.getReportProcessingTimeoutInMinutes()).thenReturn(1);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> mongoReportRepository.getReportAndChangeUpdateTime(report));
        }
        ReportEntity result = mongoReportRepository.getReport(report.getId());
        assertEquals(result.getVersion(), 1);
    }

    @Test
    void deleteOld() {
        when(configuration.getReportCleanupTimeoutInDays()).thenReturn(1);
        ReportEntity report = ReportEntity.builder()
                .id(getGuid())
                .accountNumber(getGuid())
                .updateTime(new Date(0))
                .creationTime(new Date(0))
                .reportStatus(ReportStatus.READY).build();
        mongoReportRepository.save(report);
        ReportEntity result = mongoReportRepository.getReport(report.getId());
        assertEquals(result, report);
        mongoReportRepository.deleteOld();
        result = mongoReportRepository.getReport(report.getId());
        assertNull(result);
    }

    private String getGuid() {
        return UUID.randomUUID().toString();
    }
}