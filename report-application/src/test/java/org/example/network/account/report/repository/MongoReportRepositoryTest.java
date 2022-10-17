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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.*;

class MongoReportRepositoryTest {

    private static final String CONNECTION_STRING = "mongodb://%s:%d";

    private MongodExecutable mongodExecutable;
    private MongoTemplate mongoTemplate;
    private MongoReportRepository mongoReportRepository;

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
        ReportApplicationConfigurationProperties configuration = new ReportApplicationConfigurationProperties();
        mongoReportRepository = new MongoReportRepository(mongoTemplate, configuration);
    }



    @Test
    void findAllByAccountNumber() {
        mongoReportRepository.findAllByAccountNumber("test");
    }

    @Test
    void saveTransfer() {
    }

    @Test
    void getUnfinishedRequests() {
    }

    @Test
    void getReportAndChangeUpdateTime() {
    }

    @Test
    void getReport() {
    }

    @Test
    void save() {
    }

    @Test
    void deleteOld() {
    }
}