package org.example.network.account.domain.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OperationResponse {

    private Status status;

}
