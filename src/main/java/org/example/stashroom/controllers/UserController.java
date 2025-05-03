package org.example.stashroom.controllers;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.stashroom.dto.AuthDTO;
import org.example.stashroom.dto.UserCreateDTO;
import org.example.stashroom.dto.UserDTO;
import org.example.stashroom.dto.UserUpdateDTO;
import org.example.stashroom.services.SecurityService;
import org.example.stashroom.services.UserService;
import org.example.stashroom.utils.AuthTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SecurityService securityService;
    private final AuthTokenProvider authTokenProvider;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserCreateDTO dto) {
        UserDTO createdUser = userService.register(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.id())
                .toUri();
        return ResponseEntity.created(location).body(createdUser);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody AuthDTO dto) {
        UserDTO user = userService.authenticate(dto);
        String token = authTokenProvider.generateToken(user.username());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto) {
        securityService.validateOwnerOrAdmin(id);
        UserDTO updatedUser = userService.updateProfile(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO currentUser = securityService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }
}
