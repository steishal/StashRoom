package org.example.stashroom.services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stashroom.dto.PostDTO;
import org.example.stashroom.dto.UserDTO;
import org.example.stashroom.exceptions.NotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityService {
    private final UserService userService;

    public String getCurrentUserUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthenticated access attempt");
            throw new AuthenticationCredentialsNotFoundException("User not authenticated");
        }

        return authentication.getName();
    }

    public UserDTO getCurrentUser() {
        String username = getCurrentUserUsername();
        try {
            return userService.findByUsername(username);
        } catch (NotFoundException ex) {
            log.error("User {} not found in DB but present in security context", username);
            throw new AuthenticationServiceException("User data inconsistency");
        }
    }

    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;

        String formattedRole = "ROLE_" + role.toUpperCase();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority ->
                        grantedAuthority.getAuthority().equals(formattedRole));
    }

    public boolean isOwner(Long userId) {
        UserDTO currentUser = getCurrentUser();
        return currentUser.getId().equals(userId);
    }

    public boolean isOwner(String username) {
        return getCurrentUserUsername().equals(username);
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public void validateOwnerOrAdmin(Long userId) {
        UserDTO currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId) && !hasRole("ADMIN")) {
            log.warn("Access denied for user {} to resource {}", currentUser.getId(), userId);
            throw new AccessDeniedException("Access denied");
        }
    }

    public void validateOwnerOrAdmin(String username) {
        if (!isOwner(username) && !hasRole("ADMIN")) {
            log.warn("Access denied for user {} to resource {}", getCurrentUserUsername(), username);
            throw new AccessDeniedException("Access denied");
        }
    }
}
