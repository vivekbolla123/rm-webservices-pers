package com.akasaair.rm_webservice.file_generation.controller;

import com.akasaair.rm_webservice.common.APIResponse;
import com.akasaair.rm_webservice.common.AuthUtils;
import com.akasaair.rm_webservice.common.exceptions.DataMismatchedException;
import com.akasaair.rm_webservice.file_generation.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.util.annotation.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.akasaair.rm_webservice.common.Constants.*;

@RestController
@RequestMapping(value = "/v1/file", produces = "application/json")
@ControllerAdvice
public class FileController {

    @Autowired
    FileService fileService;

    @PostMapping(value = "/marketList/upload", consumes = {"multipart/form-data"})
    public APIResponse<?> uploadMarketListFile(@RequestPart("file") MultipartFile file,
                                               Authentication authUser) throws Exception {
        String userName = AuthUtils.getAuthUserName(authUser);
        Object role = AuthUtils.getRoleList(authUser);
        return new APIResponse<>(fileService.uploadMarketListFile(file, MARKET_LIST_TABLE_NAME, userName, role));
    }

    @PostMapping(value = "/marketListAdhoc/upload", consumes = {"multipart/form-data"})
    public APIResponse<?> uploadMarketListAdhocFile(@RequestPart("file") MultipartFile file,
                                                    @RequestPart("name") String name,
                                                    Authentication authUser) throws Exception {
        String userName = AuthUtils.getAuthUserName(authUser);
        Object role = AuthUtils.getRoleList(authUser);
        return new APIResponse<>(fileService.uploadMarketListFile(file, MARKET_LIST_ADHOC_TABLE_NAME + UNDERSCORE + name, userName, role));
    }

    @PostMapping(value = "/strategy/upload", consumes = {"multipart/form-data"})
    public APIResponse<?> uploadStrategyGridFile(@RequestPart("file") MultipartFile file,
                                                 Authentication authUser) throws DataMismatchedException, IOException {
        String userName = AuthUtils.getAuthUserName(authUser);
        AuthUtils.getRoleList(authUser);
        return new APIResponse<>(fileService.uploadStrategyGridFile(file, userName));
    }

    @PostMapping(value = "/{value}/upload", consumes = {"multipart/form-data"})
    public APIResponse<?> uploadFile(@RequestPart("file") MultipartFile file,
                                     @Valid @NotEmpty @PathVariable String value,
                                     Authentication authUser) throws DataMismatchedException, IOException {
        String tableName = "";
        if (RM_TABLE_NAMES.containsKey(value)) {
            tableName = RM_TABLE_NAMES.get(value);
        }
        String userName = AuthUtils.getAuthUserName(authUser);
        AuthUtils.getRoleList(authUser);
        return new APIResponse<>(fileService.uploadFile(file, tableName, userName));
    }

    @GetMapping("/download/{value}")
    public ResponseEntity<?> downloadCSV(@Valid @NotEmpty @PathVariable String value,
                                         @Nullable @RequestParam("name") String name) throws DataMismatchedException {
        String tableName = RM_TABLE_NAMES.get(value);
        String filename = tableName + ".csv";
        if (tableName.equalsIgnoreCase(MARKET_LIST_ADHOC_TABLE_NAME)) {
            tableName = tableName + UNDERSCORE + name;
        }
        InputStreamResource file = new InputStreamResource(fileService.createCSV(tableName));

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType("text/csv"));
        header.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok()
                .headers(header)
                .body(file);
    }

    @GetMapping("/view/{value}")
    public APIResponse<?> viewValues(@Valid @NotEmpty @PathVariable String value,
                                     @Nullable @RequestParam("name") String name,
                                     @Nullable @RequestParam("curveId") String curveId,
                                     @Nullable @RequestParam("inputId") String inputId,
                                     Authentication authUser) throws DataMismatchedException {
        AuthUtils.getRoleList(authUser);
        String tableName = RM_TABLE_NAMES.get(value);
        if (tableName.equalsIgnoreCase(MARKET_LIST_ADHOC_TABLE_NAME)) {
            tableName = tableName + UNDERSCORE + name;
        }
        return new APIResponse<>(fileService.createListOfValues(tableName, curveId, inputId));
    }

    @GetMapping("/update/files/{value}")
    public APIResponse<?> updateInputFiles(@Valid @NotEmpty @PathVariable String value,
                                           @Nullable @RequestParam("name") String name,
                                           Authentication authUser) {
        AuthUtils.getRoleList(authUser);
        String tableName = RM_TABLE_NAMES.get(value);
        if (tableName.equalsIgnoreCase(MARKET_LIST_ADHOC_TABLE_NAME)) {
            tableName = tableName + UNDERSCORE + name;
        }
        return new APIResponse<>(fileService.updateInputFiles(tableName));
    }

}
