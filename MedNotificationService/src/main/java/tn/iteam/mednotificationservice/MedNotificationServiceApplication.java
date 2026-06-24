package tn.iteam.mednotificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class MedNotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedNotificationServiceApplication.class, args);
    }

}
