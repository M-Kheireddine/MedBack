package tn.iteam.meduserservice.services.impls;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import tn.iteam.meduserservice.dtos.requests.SigninRequestDto;
import tn.iteam.meduserservice.dtos.requests.SignupRequestDto;
import tn.iteam.meduserservice.dtos.responses.AuthResponseDto;
import tn.iteam.meduserservice.models.AccountEntity;
import tn.iteam.meduserservice.models.UserEntity;
import tn.iteam.meduserservice.repositories.AccountRepository;
import tn.iteam.meduserservice.repositories.UserRepository;
import tn.iteam.meduserservice.services.specs.IAuthService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {

    private final Keycloak keycloak;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Override
    @Transactional
    public void signup(SignupRequestDto request) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstname());
        user.setLastName(request.getLastname());
        user.setEmail(request.getEmail());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        user.setCredentials(Collections.singletonList(credential));

        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        Response response = usersResource.create(user);
        
        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to create user in Keycloak, status: " + response.getStatus());
        }

        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        
        // Assign Role
        try {
            RoleRepresentation roleRep = realmResource.roles().get(request.getRole()).toRepresentation();
            UserResource userResource = usersResource.get(userId);
            userResource.roles().realmLevel().add(Collections.singletonList(roleRep));
        } catch (Exception e) {
            log.error("Failed to assign role: ", e);
            throw new RuntimeException("Failed to assign role");
        }

        // Save locally
        UUID userUuid = UUID.fromString(userId);

        UserEntity userEntity = UserEntity.builder()
                .userId(userUuid)
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .build();
        userRepository.save(userEntity);

        AccountEntity accountEntity = AccountEntity.builder()
                .accountId(userUuid)
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword()) // Optional, maybe hash it or ignore it
                .build();
        accountRepository.save(accountEntity);
    }

    @Override
    public AuthResponseDto signin(SigninRequestDto request) {
        String tokenUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", request.getUsername());
        formData.add("password", request.getPassword());

        try {
            return webClientBuilder.build().post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(AuthResponseDto.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to authenticate with Keycloak", e);
            throw new RuntimeException("Authentication failed");
        }
    }
}
