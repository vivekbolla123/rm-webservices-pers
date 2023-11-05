package com.akasaair.rm_webservice.auth.service;

import com.akasaair.rm_webservice.auth.dto.ConfigDto;
import com.akasaair.rm_webservice.common.config.RmAllocDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.akasaair.rm_webservice.common.Constants.*;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RmAllocDao rmAllocDao;

    @Override
    public ConfigDto getuserConfigDetails(Object role) {
        ConfigDto configDto = new ConfigDto();
        configDto.setRole(null);
        configDto.setPermissions(null);
        if (role != null) {
            configDto.setRole(rmAllocDao.getRoleName(role).get(0));
            configDto.setPermissions(rmAllocDao.getRoleAccess(role));
        }
        return configDto;
    }
}