package org.example.network.account.report.model.entity;

import lombok.Builder;
import lombok.Data;
import org.example.network.account.report.model.dto.MoneyTransfer;
import org.example.network.account.report.model.dto.ReportStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.List;

@Document
@Data
@Builder
public class ReportEntity {

    @Id
    private String id;

    private String accountNumber;

    @Version
    private int version;

    private OffsetDateTime creationTime;

    private OffsetDateTime updateTime;

    private List<TransferEntity> transferList;

    private ReportStatus reportStatus;

}
