package org.example.network.account.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferRequest {

    private String reqeustId;

    private String sourceAccountId;

    private String destinationAccountId;

    private BigDecimal amount;

    private String currency;

}
