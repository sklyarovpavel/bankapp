package org.example.network.account.domain.dto;

public enum Status {

    SUCCESS,
    SOURCE_ACCOUNT_NOT_FOUND,
    DESTINATION_ACCOUNT_NOT_FOUND,
    SOURCE_ACCOUNT_INSUFFICIENT_FUNDS,
    LOCK_EXCEPTION,
    TIMEOUT,
    ALREADY_EXISTS,

}
