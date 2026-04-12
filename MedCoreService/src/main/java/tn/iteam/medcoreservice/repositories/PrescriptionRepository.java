package tn.iteam.medcoreservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.iteam.medcoreservice.models.PrescriptionEntity;

@Repository
public interface PrescriptionRepository extends MongoRepository<PrescriptionEntity, String> {
}
