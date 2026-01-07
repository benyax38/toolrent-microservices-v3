package com.example.loan_return_service.Client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ClientServiceRestClient {

    private final RestTemplate restTemplate;

    // Idealmente configurable por application.yml
    private static final String CLIENT_SERVICE_URL =
            "http://client-service/api/clients";

    public void restrictClient(Long clientId) {
        String url = CLIENT_SERVICE_URL + "/restrict/" + clientId;
        restTemplate.put(url, null);
    }
}
