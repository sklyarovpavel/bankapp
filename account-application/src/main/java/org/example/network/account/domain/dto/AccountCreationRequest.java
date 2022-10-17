package org.example.network.account.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountCreationRequest {

    private String reqeustId;

    private String accountId;

    private String currency;

}
