package com.app.test.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum InternalErrorCode {
    //da 100 a 199 - errori di validazione
    PARAMETER_REQUIRED(100, "Parameter required"),
    PARAMETER_INVALID(101, "Parameter invalid"),

    //da 200 a 299 - errori di business
    CONFLICT(200, "Conflict"),

    //da 300 a 399 - errori di sistema
    INTERNAL_SEVER_ERROR(500, "Internal server error"),
    METHOD_INVOCATION_FAILED(501, "Method invocation failed"),
    FIELD_ACCESS_DENIED(502, "Field access denied"),
    FIELD_VALUE_PARSE_ERROR(503, "Field value parse error"),





    SYSTEM_LOCKED(504, "System locked"),
    //da 400 a 499 - errori di risorse
    RESOURCE_NOT_FOUND(400, "Resource not found"),
    UNAUTHORIZED(401, "Unauthorized"),
    FIELD_NOT_FOUND(402, "Field not found"),
    METHOD_NOT_FOUND(403, "Method not found"),

    //da 500 a 599 - errori di sicurezza

    //da 600 a 699 - errori di autenticazione

    //da 700 a 799 - errori di autorizzazione
    FORBIDDEN(700, "Access denied"),

    //da 800 in poi - errori generici personalizzabili
    UNHANDLED_ERROR(800, "Unhandled error");


    private int code;
    private String message;
}
