package com.akasaair.rm_webservice.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse<T> {
    T data;
    Error error;

    public APIResponse(T data) {
        this.data = data;
    }

    public APIResponse(Error error) {
        this.error = error;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Error {
        private HttpStatus status;
        private String code;
        @Builder.Default
        private Date timestamp = new Date();
        private List<?> message;
    }
}
