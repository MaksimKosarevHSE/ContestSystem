package com.maksim.auth_service.repository;

import com.maksim.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);

    boolean existsByHandle(String handle);

    Optional<User> findByEmail(String email);
}
