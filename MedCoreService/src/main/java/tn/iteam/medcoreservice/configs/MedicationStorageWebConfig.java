package tn.iteam.medcoreservice.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MedicationStorageWebConfig implements WebMvcConfigurer {
    private final Path medicationImageDirectory;

    public MedicationStorageWebConfig(
            @Value("${medback.storage.medication-image-directory:./storage/medications}") String medicationImageDirectory
    ) {
        this.medicationImageDirectory = Paths.get(medicationImageDirectory).toAbsolutePath().normalize();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/medications/**")
                .addResourceLocations(resolveResourceLocation());
    }

    private String resolveResourceLocation() {
        String resourceLocation = this.medicationImageDirectory.toUri().toString();
        return resourceLocation.endsWith("/") ? resourceLocation : resourceLocation + "/";
    }
}
