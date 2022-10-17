package org.example.network.account.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DepositRequest {

    private String reqeustId;

    private String accountId;

    private BigDecimal amount;

    private String currency;

}
