package ro.tuc.ds2020.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TokenValidationService {

    private final RestTemplate restTemplate;

    public TokenValidationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean validateToken(String token) {
        String personServiceUrl = "http://user-microservice-spring:8080/person/validate-token";
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    personServiceUrl,
                    Map.of("token", token),
                    Map.class
            );
            Map<String, Object> responseBody = response.getBody();
            return response.getStatusCode() == HttpStatus.OK && Boolean.TRUE.equals(responseBody != null ? responseBody.get("valid") : null);
        } catch (Exception e) {
            System.err.println("Error validating token: " + e.getMessage());
            return false;
        }
    }

}