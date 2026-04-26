package tn.iteam.meddiscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MedDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedDiscoveryApplication.class, args);
    }

}
