package org.example.stashroom.controllers;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.stashroom.dto.*;
import org.example.stashroom.services.SecurityService;
import org.example.stashroom.services.UserService;
import org.example.stashroom.utils.AuthTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

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

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String query) {
        List<UserDTO> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserCreateDTO dto) {
        UserDTO createdUser = userService.register(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdUser);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody AuthDTO dto) {
        UserDTO user = userService.authenticate(dto);
        String token = authTokenProvider.generateToken(user.getUsername(), user.getRole());
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

    @PostMapping(value = "/avatar", consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AvatarUploadDTO> uploadAvatar(
            @RequestParam("userId") Long userId,
            @RequestParam("avatar") MultipartFile avatarFile) {

        AvatarUploadDTO avatarDto = userService.uploadAvatar(userId, avatarFile);
        return ResponseEntity.ok(avatarDto);
    }

    @GetMapping("/{userId}/avatar")
    public ResponseEntity<AvatarUploadDTO> getAvatar(@PathVariable Long userId) {
        AvatarUploadDTO avatarDTO = userService.getAvatarByUserId(userId);
        return ResponseEntity.ok(avatarDTO);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }
}
