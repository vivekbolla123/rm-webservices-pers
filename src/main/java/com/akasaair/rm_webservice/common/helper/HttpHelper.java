package com.akasaair.rm_webservice.common.helper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

@Slf4j
@Component
public class HttpHelper {

    @Value("${api.access-key}")
    String apiKey;
    @Value("${api.url}")
    String apiURL;

    public void makeLambdaAPICall(ObjectNode request) {
        WebClient webClient = WebClient.create();
        try {
            String response = webClient.post()
                    .uri(new URI(apiURL))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-api-key", apiKey)
                    .body(BodyInserters.fromValue(request))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("SUCCESS, {}", response);
        } catch (Exception e) {
            log.error("Error from response:" + e);
            throw new RuntimeException(e);
        }
    }
}