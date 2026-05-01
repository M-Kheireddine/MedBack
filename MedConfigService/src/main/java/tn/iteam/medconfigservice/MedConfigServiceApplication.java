package tn.iteam.medconfigservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class MedConfigServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedConfigServiceApplication.class, args);
    }

}
