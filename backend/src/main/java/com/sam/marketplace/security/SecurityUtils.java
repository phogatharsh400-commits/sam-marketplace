package com.sam.marketplace.security;

import com.sam.marketplace.common.enums.RoleType;
import com.sam.marketplace.common.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal;
        }
        throw new UnauthorizedException("User is not authenticated");
    }

    public static UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static RoleType getCurrentUserRole() {
        return getCurrentUser().getRole();
    }

    public static boolean hasRole(RoleType role) {
        try {
            return getCurrentUserRole() == role;
        } catch (Exception e) {
            return false;
        }
    }
}
