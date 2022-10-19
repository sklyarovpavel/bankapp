package org.example.network.account.report.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@Builder
public class TransferEntity {

    @Id
    private String id;

    private String accountId;

    private BigDecimal amount;

}
