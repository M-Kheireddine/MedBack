package tn.iteam.medcoreservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tn.iteam.medcoreservice.models.PrescriptionEntity;
import tn.iteam.medcoreservice.repositories.PrescriptionRepository;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class MedCoreServiceApplication implements CommandLineRunner {
	@Autowired
	private PrescriptionRepository prescriptionRepository;

	public static void main(String[] args) {
		SpringApplication.run(MedCoreServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		this.prescriptionRepository.deleteAll();
		this.prescriptionRepository.saveAll(List.of(
				PrescriptionEntity.builder().doctorId(UUID.randomUUID().toString()).patientId(UUID.randomUUID().toString()).note("Note #001").build(),
				PrescriptionEntity.builder().doctorId(UUID.randomUUID().toString()).patientId(UUID.randomUUID().toString()).note("Note #002").build(),
				PrescriptionEntity.builder().doctorId(UUID.randomUUID().toString()).patientId(UUID.randomUUID().toString()).note("Note #003").build()
		));
	}
}
