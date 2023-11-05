package com.akasaair.rm_webservice.auth.service;

import com.akasaair.rm_webservice.auth.dto.ConfigDto;

public interface AuthService {

    ConfigDto getuserConfigDetails(Object role);
}
