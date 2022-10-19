package org.example.network.account.report.repository;

import lombok.RequiredArgsConstructor;
import org.example.network.account.report.api.repository.ReportRepository;
import org.example.network.account.report.api.repository.TransferRepository;
import org.example.network.account.report.configuration.ReportApplicationConfigurationProperties;
import org.example.network.account.report.model.dto.ReportStatus;
import org.example.network.account.report.model.entity.ReportEntity;
import org.example.network.account.report.model.entity.TransferEntity;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class MongoReportRepository implements ReportRepository, TransferRepository {

    public static final FindAndModifyOptions RETURN_NEW = FindAndModifyOptions.options().returnNew(true);
    private final MongoTemplate mongoTemplate;

    private final ReportApplicationConfigurationProperties configurationProperties;

    @Override
    public List<TransferEntity> findAllByAccountId(String accountId) {
        Criteria criteria = new Criteria("accountId").is(accountId);
        return mongoTemplate.find(new Query(criteria), TransferEntity.class);
    }

    @Override
    public void saveTransfer(TransferEntity transferEntity) {
        mongoTemplate.save(transferEntity);
    }

    @Override
    public List<ReportEntity> getUnfinishedRequests() {
        Criteria criteria = new Criteria("reportStatus").is(ReportStatus.NOT_YET_READY);
        return mongoTemplate.find(new Query(criteria), ReportEntity.class);
    }

    @Override
    public ReportEntity getReportAndChangeUpdateTime(ReportEntity report) {
        OffsetDateTime timeCondition = OffsetDateTime.now().minusMinutes(configurationProperties.getReportProcessingTimeoutInMinutes());
        Criteria criteria = new Criteria("id").is(report.getId()).and("updateTime")
                .lte(Date.from(timeCondition.toInstant()));
        Update update = new Update();
        update.set("updateTime", new Date());
        return mongoTemplate.findAndModify(new Query(criteria), update, RETURN_NEW, ReportEntity.class);
    }

    @Override
    public ReportEntity getReport(String reportId) {
        return mongoTemplate.findById(reportId, ReportEntity.class);
    }

    @Override
    public void save(ReportEntity report) {
        mongoTemplate.save(report);
    }

    @Override
    public void deleteOld() {
        OffsetDateTime timeCondition = OffsetDateTime.now().minusDays(configurationProperties.getReportCleanupTimeoutInDays());
        Criteria criteria = new Criteria("reportStatus")
                .is(ReportStatus.READY)
                .and("updateTime")
                .lte(Date.from(timeCondition.toInstant()));
        mongoTemplate.remove(new Query(criteria), ReportEntity.class);
    }
}
