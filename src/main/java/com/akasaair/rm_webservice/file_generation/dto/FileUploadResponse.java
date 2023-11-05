package com.akasaair.rm_webservice.file_generation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class FileUploadResponse {
    private int line;
    private String error;
    private List<String> marketListEntity;

}
