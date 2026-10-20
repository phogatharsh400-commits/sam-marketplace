package com.sam.marketplace.user;

import com.sam.marketplace.common.enums.AccountStatus;
import com.sam.marketplace.common.enums.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByMobileNumber(String mobileNumber);
    Optional<User> findByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
    boolean existsByEmail(String email);
    Page<User> findByRole(RoleType role, Pageable pageable);
    Page<User> findByStatus(AccountStatus status, Pageable pageable);
    Page<User> findByRoleAndStatus(RoleType role, AccountStatus status, Pageable pageable);
}
