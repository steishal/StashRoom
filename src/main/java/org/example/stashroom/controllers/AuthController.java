package org.example.stashroom.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.stashroom.dto.ForgotPasswordRequest;
import org.example.stashroom.entities.User;
import org.example.stashroom.services.TelegramRestClient;
import org.example.stashroom.services.UserService;
import org.example.stashroom.utils.RateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final TelegramRestClient telegramRestClient;
    private final RateLimiter rateLimiter;

    public AuthController(UserService userService, TelegramRestClient telegramRestClient, RateLimiter rateLimiter) {
        this.userService = userService;
        this.telegramRestClient = telegramRestClient;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}

        Optional<User> userOpt = userService.findByEmailAndPhone(
                request.getEmail(),
                request.getPhone()
        );

        if (userOpt.isEmpty() || userOpt.get().getTelegramChatId() == null) {
            log.warn("Password reset attempt for invalid credentials");
            return ResponseEntity.ok().build();
        }

        User user = userOpt.get();

        if (!rateLimiter.tryAcquire(String.valueOf(user.getId()))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Слишком много запросов. Попробуйте позже.");
        }

        String token = userService.generateTelegramToken(user.getId());

        String message = "Ваш токен для сброса пароля: " + token +
                "\nТокен действителен 10 минут";
        telegramRestClient.sendMessage(Long.valueOf(user.getTelegramChatId()), message);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam String token,
            @RequestBody String newPassword
    ) {
        boolean result = userService.resetPasswordWithToken(token, newPassword);
        return result
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().body("Неверный токен");
    }
}
