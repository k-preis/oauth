package com.preis.authcodeflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class OAuthManager {

    @Value("${auth0.clientId}")
    private String clientId;

    @Value("${auth0.clientSecret}")
    private String clientSecret;

    @Value("${auth0.scope}")
    private String scope;

    @Value("${auth0.audience}")
    private String audience;

    @Value("${auth0.redirectUri}")
    private String redirectUri;

    private final RestTemplate restTemplate;

    private Map<String, Object> userDataMap;

    private final ObjectMapper objectMapper;

    public OAuthManager(RestTemplate restTemplate, ObjectMapper objectmapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectmapper;
    }

    public String generateAuthorizeUser() {
        StringBuilder url = new StringBuilder();
        url.append("https://dev-q7dzvu43orxj3t1e.us.auth0.com/authorize");
        url.append("?response_type=code");
        url.append("&client_id=" + clientId);
        url.append("&redirect_uri=" + redirectUri);
        url.append("&scope=" + scope);
        url.append("&audience=" + audience);
        url.append("&state=xyzABC123");
        return url.toString();
    }

    public ResponseEntity<String> loginUserByCode(String code) throws JsonProcessingException {
        String url = "https://dev-q7dzvu43orxj3t1e.us.auth0.com/oauth/token";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "authorization_code");
        requestBody.put("audience", audience);
        requestBody.put("scope", scope);
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("redirect_uri", redirectUri);
        requestBody.put("code", code);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        String body = response.getBody();


        JsonNode jsonNode = objectMapper.readTree(body);
        String idToken = jsonNode.get("id_token").asText();
        String userJson = decode(idToken);

        userDataMap = objectMapper.readValue(userJson, new TypeReference<>() {
        });


        return response;
    }

    public Map<String, Object> getUserData() {
        return userDataMap;
    }

    private String decode(String token) {
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjhoR2NhS3RkeUNnT1YwTUtVUFdKdiJ9.eyJuaWNrbmFtZSI6Inp1bWJpLnJldiIsIm5hbWUiOiJ6dW1iaS5yZXZAZ21haWwuY29tIiwicGljdHVyZSI6Imh0dHBzOi8vcy5ncmF2YXRhci5jb20vYXZhdGFyLzVkNTgzYjIyNjBkMTVjNDRjNzRhMmM4NTYxNGM2MjBkP3M9NDgwJnI9cGcmZD1odHRwcyUzQSUyRiUyRmNkbi5hdXRoMC5jb20lMkZhdmF0YXJzJTJGenUucG5nIiwidXBkYXRlZF9hdCI6IjIwMjMtMTEtMDRUMTU6NTk6NTQuMzI5WiIsImVtYWlsIjoienVtYmkucmV2QGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJpc3MiOiJodHRwczovL2Rldi1xN2R6dnU0M29yeGozdDFlLnVzLmF1dGgwLmNvbS8iLCJhdWQiOiJXNUFRWWlxNTN0Qm5CMkJMZktCQm1iWGhqU0JEaFVLWiIsImlhdCI6MTY5OTExOTY1OSwiZXhwIjoxNjk5MTU1NjU5LCJzdWIiOiJhdXRoMHw2NTFkOTg4MDFmMDM0ZDdiYTcwODZmZWYiLCJzaWQiOiJXQ211ekxITGM4cExYRklTS1I0VFdCTkNRTURkQURObiJ9.ej7JLtoHCG2Lj3ua7BFJZR-B72sVmv6FyL67JyVswqESOczwMioFIiSr-PNaUIyRXKZabKYSzZmzRHF8L-i5A5RwCprndG6W8VFRnYuEna4xGh7m5YkTeyEq3CWipWlBSR2mHma0wkAUCEN2dkFFOIlH5CdjWg09lukuk3EOzOIyfgfEFpKxERhe3L8Z5QS7KsUlklM__y5NNzU6Xo-aMmgyvAHlxdohbIeFWXjyDsgRmH0OP9BOgcXhnIH3nxNnzJhMN2wU8NVrBq3Lw8WW4Ip-Yy6nbfCPb1t1lB9Y3k3v5_07lZhdKdwHW4hSkKDXyWsRYujLix2yHK9z0VYGpA";
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        return payload;
    }
}
