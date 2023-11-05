package com.akasaair.rm_webservice.common.exceptions;

import java.util.List;

public class DataMismatchedException extends Exception {

    List<?> message;

    public DataMismatchedException(List<?> message){
        this.message=message;
    }
}
