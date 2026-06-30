package tn.iteam.medcoreservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.iteam.medcoreservice.models.Medication;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface MedicationRepository extends MongoRepository<Medication, String> {
    @Query("{ '$or': [ { 'name': { $regex: ?0, $options: 'i' } }, { 'category': { $regex: ?0, $options: 'i' } }, { 'laboratory': { $regex: ?0, $options: 'i' } } ] }")
    List<Medication> search(String query);

    @Query(
            value = "{ '$or': [ { 'name': { $regex: ?0, $options: 'i' } }, { 'category': { $regex: ?0, $options: 'i' } }, { 'laboratory': { $regex: ?0, $options: 'i' } } ] }",
            fields = "{ '_id': 1, 'name': 1, 'category': 1 }"
    )
    List<Medication> autocomplete(String query, Pageable pageable);
}
