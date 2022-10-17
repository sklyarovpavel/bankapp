package org.example.network.account.report.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MoneyTransfer {

    private String id;

    private BigDecimal amount;

    private String accountId;

    private String currency;
}
