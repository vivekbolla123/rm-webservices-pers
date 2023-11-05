package com.akasaair.rm_webservice.common.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UnauthorizedException extends RuntimeException {
    String message;
}
