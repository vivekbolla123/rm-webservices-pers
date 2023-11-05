package com.akasaair.rm_webservice.common.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResourceNotFoundException extends RuntimeException {
    String message;
}
