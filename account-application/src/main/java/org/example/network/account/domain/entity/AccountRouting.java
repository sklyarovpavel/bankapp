package org.example.network.account.domain.entity;

import lombok.Data;

@Data
public class AccountRouting {

    private long id;

    private String accountId;

    private String shardId;

}
