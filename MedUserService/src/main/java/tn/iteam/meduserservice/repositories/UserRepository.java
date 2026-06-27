package tn.iteam.meduserservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.iteam.meduserservice.models.Role;
import tn.iteam.meduserservice.models.UserEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(Role role);
}
