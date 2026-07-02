package tn.iteam.medcoreservice.clients;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tn.iteam.medcoreservice.clients.dto.InternalDoctorProfileDto;
import tn.iteam.medcoreservice.clients.dto.InternalPatientProfileDto;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserProfileClient {
    private static final String USER_SERVICE_ID = "med-user-service";
    private static final String INTERNAL_DOCTOR_PROFILE_URI = "/api/v1/internal/doctors/{doctorId}/profile";
    private static final String INTERNAL_PATIENT_PROFILE_URI = "/api/v1/internal/patients/{patientId}/profile";

    private final DiscoveryClient discoveryClient;

    public InternalDoctorProfileDto getDoctorProfile(String doctorId) {
        return buildRestClient().get()
                .uri(INTERNAL_DOCTOR_PROFILE_URI, doctorId)
                .retrieve()
                .body(InternalDoctorProfileDto.class);
    }

    public InternalPatientProfileDto getPatientProfile(String patientId) {
        return buildRestClient().get()
                .uri(INTERNAL_PATIENT_PROFILE_URI, patientId)
                .retrieve()
                .body(InternalPatientProfileDto.class);
    }

    private RestClient buildRestClient() {
        return RestClient.builder()
                .baseUrl(resolveUserServiceBaseUrl())
                .build();
    }

    private String resolveUserServiceBaseUrl() {
        List<ServiceInstance> instances = discoveryClient.getInstances(USER_SERVICE_ID);
        if (instances == null || instances.isEmpty()) {
            throw new IllegalStateException("MedUserService is not available in service discovery.");
        }

        String baseUrl = instances.getFirst().getUri().toString();
        log.debug("Resolved MedUserService base URL: {}", baseUrl);
        return baseUrl;
    }
}
