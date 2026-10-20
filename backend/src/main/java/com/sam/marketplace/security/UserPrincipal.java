package com.sam.marketplace.security;

import com.sam.marketplace.common.enums.AccountStatus;
import com.sam.marketplace.common.enums.RoleType;
import com.sam.marketplace.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String mobileNumber;
    private final String email;
    private final String fullName;
    private final String password;
    private final RoleType role;
    private final AccountStatus status;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(UUID id, String mobileNumber, String email, String fullName, String password,
                         RoleType role, AccountStatus status, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.role = role;
        this.status = status;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        return new UserPrincipal(
                user.getId(),
                user.getMobileNumber(),
                user.getEmail(),
                user.getFullName(),
                user.getPasswordHash(),
                user.getRole(),
                user.getStatus(),
                Collections.singletonList(authority)
        );
    }

    public UUID getId() { return id; }
    public String getMobileNumber() { return mobileNumber; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public RoleType getRole() { return role; }
    public AccountStatus getStatus() { return status; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return mobileNumber; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() {
        return status != AccountStatus.SUSPENDED && status != AccountStatus.REJECTED;
    }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
