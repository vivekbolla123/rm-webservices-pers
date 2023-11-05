package com.akasaair.rm_webservice.auth.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfigDto {
    private String role;
    private List<String> permissions;
}
