package com.tournament.exception;

/** Исключение для операции, запрещённой правилами предметной области. */
public class BusinessRuleException extends TournamentException {
    public BusinessRuleException(String message, String suggestion) {
        this(message, suggestion, "BUSINESS_RULE_VIOLATION");
    }

    public BusinessRuleException(String message, String suggestion, String errorCode) {
        super(message, errorCode, suggestion);
    }
}
