package org.example.network.account.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountEntity {

    private String id;

    private BigDecimal amount;

    private String currency;

    private int version;

}
