package com.aspireapp.loan.constants;

public interface ResponseCode {
    String SUCCESS = "100";
    String SUCCESS_200 = "200";
    String SUCCESS_CREATED = "201";
    String TRANSACTION_NOT_FOUND = "300";
    String DUPLICATE_ORDER_ID = "301";
    String MERCHANT_ID_REQUIRED = "302";
    String ACCOUNT_NUMBER_WITH_IFSC_CODE_OR_UPI_REQUIRED = "303";
    String WALLET_NOT_APPROVED = "304";
    String INSUFFICIENT_BALANCE = "305";
    String INVALID_STORE_ID = "306";
    String INVALID_BHARATPE_TXN_ID = "307";
    String INVALID_DATA = "308";
    String CLIENT_NOT_FOUND = "309";
    String INVALID_PARENT_ID = "310";
    String INVALID_REMITTANCE_TYPE = "311";
    String FAILURE_FROM_BANK = "312";
    String INVALID_VPA = "313";
    String REFUND_AMT_GREATER_THAN_TXN_AMT = "314";
    String COMPLETED_TRANSACTION = "315";
    String TRANSACTION_ID_REQD = "316";
    String UNAUTHORIZED = "401";
    String NOT_FOUND = "404";
    String SOMETHING_WENT_WRONG = "501";
    String BAD_REQUEST = "400";
    String DUPLICATE_REQUEST = "409";
    String MERCHANT_LEVEL_VPA_CREATION_NOT_ALLOWED = "317";
    String STORE_NOT_FOUND = "318";
    String DUPLICATE_ENTRY = "319";
    String MERCHANT_TERMINAL_COUNT_CANNOT_EXCEED_50 = "320";
    String COLLECT_VPA_NOT_CONFIGURED = "321";
}