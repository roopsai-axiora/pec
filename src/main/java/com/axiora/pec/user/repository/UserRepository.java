package com.axiora.pec.user.repository;

import com.axiora.pec.user.domain.Role;
import com.axiora.pec.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRoleAndActiveTrueOrderByFullNameAsc(Role role);

    List<User> findByRoleAndActiveTrueAndManagerIdOrderByFullNameAsc(
            Role role, Long managerId);

    @Query("""
            SELECT u FROM User u
            WHERE u.role = :role
              AND u.active = true
              AND (
                    LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                  )
            ORDER BY u.fullName ASC
            """)
    List<User> searchActiveUsersByRole(Role role, String search);

    @Query("""
            SELECT u FROM User u
            WHERE u.role = :role
              AND u.active = true
              AND u.manager.id = :managerId
              AND (
                    LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                  )
            ORDER BY u.fullName ASC
            """)
    List<User> searchActiveUsersByRoleAndManagerId(
            Role role, Long managerId, String search);
}
