package ro.tuc.ds2020.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class TokenValidationService {

    private final RestTemplate restTemplate;

    public TokenValidationService() {
        this.restTemplate = new RestTemplate();
    }

    public boolean validateToken(String token) {
        System.out.println("Validating " + token);
        String personServiceUrl = "http://user-microservice-spring:8080/person/validate-token";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    personServiceUrl,
                    HttpMethod.POST,
                    requestEntity,
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