package ci.hardwork.gestionrdvservice.core.service.impl;

import ci.hardwork.gestionrdvservice.core.dto.UserReference;
import ci.hardwork.gestionrdvservice.core.service.UserReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserReferenceServiceImpl implements UserReferenceService {

    private final RestTemplate restTemplate;

    @Value("${app.auth.service-url}")
    private String authServiceUrl;

    @Override
    public Optional<UserReference> getUserById(Long userId) {
        try {
            String url = authServiceUrl + "/users/" + userId;
            ResponseEntity<UserReference> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<UserReference>() {}
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Optional.of(response.getBody());
            }
        } catch (RestClientException e) {
            log.error("Erreur lors de la récupération de l'utilisateur avec ID {}: {}", userId, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserReference> getUserByEmail(String email) {
        try {
            String url = authServiceUrl + "/users/by-email?email=" + email;
            ResponseEntity<UserReference> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<UserReference>() {}
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Optional.of(response.getBody());
            }
        } catch (RestClientException e) {
            log.error("Erreur lors de la récupération de l'utilisateur avec email {}: {}", email, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean userExists(Long userId) {
        return getUserById(userId).isPresent();
    }
}