package org.example.stashroom.services;
import lombok.extern.slf4j.Slf4j;
import org.example.stashroom.dto.*;
import org.example.stashroom.entities.Avatar;
import org.example.stashroom.entities.User;
import org.example.stashroom.exceptions.DuplicateEntityException;
import org.example.stashroom.exceptions.InvalidCredentialsException;
import org.example.stashroom.exceptions.NotFoundException;
import org.example.stashroom.mappers.AvatarMapper;
import org.example.stashroom.mappers.UserMapper;
import org.example.stashroom.repositories.AvatarRepository;
import org.example.stashroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@Transactional
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final AvatarRepository avatarRepository;
    private final AvatarMapper avatarMapper;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder, FileStorageService fileStorageService,
                       AvatarRepository avatarRepository, AvatarMapper avatarMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
        this.avatarRepository = avatarRepository;
        this.avatarMapper = avatarMapper;
    }

    public boolean hasTelegramChatId(Long userId) {
        String chatId = userRepository.findTelegramChatIdByUserId(userId);
        return chatId != null && !chatId.isEmpty();
    }


    public UserDTO findById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        return userRepository.findById(id)
                .map(user -> {
//                    log.info("Found user: {}", user.getUsername());
                    return userMapper.toDto(user);
                })
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new NotFoundException("User not found");
                });
    }

    @Transactional
    public UserDTO register(UserCreateDTO dto) {
        log.info("Registering new user: {}", dto.username());

        userRepository.findByUsernameIgnoreCase(dto.username())
                .ifPresent(user -> {
                    log.error("Username already exists: {}", dto.username());
                    throw new DuplicateEntityException("Username already exists");
                });

        if (userRepository.existsByEmail(dto.email())) {
            log.error("Email already registered: {}", dto.email());
            throw new DuplicateEntityException("Email already registered");
        }

        User user = userMapper.fromCreateDto(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole("ROLE_USER");

        User saved = userRepository.save(user);
        log.info("User registered successfully: {}", saved.getId());
        return userMapper.toDto(saved);
    }

    public UserDTO authenticate(AuthDTO dto) {
        log.debug("Authenticating user: {}", dto.email());
        User user = userRepository.findByEmailIgnoreCase(dto.email())
                .orElseThrow(() -> {
                    log.error("Authentication failed for user: {}", dto.email());
                    return new InvalidCredentialsException();
                });

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            log.error("Invalid password for user: {}", dto.email());
            throw new InvalidCredentialsException();
        }

        log.info("User authenticated: {}", dto.email());
        return userMapper.toDto(user);
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO updateProfile(Long userId, UserUpdateDTO dto) {
        log.info("Updating profile for user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found: {}", userId);
                    return new NotFoundException("User not found");
                });

        updateEmail(user, dto.email());
        updatePhoneNumber(user, dto.phoneNumber());
        updateSocialLinks(user, dto);

        User updated = userRepository.save(user);
        log.debug("Profile updated successfully");
        return userMapper.toDto(updated);
    }

    private void updateEmail(User user, String newEmail) {
        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            if (userRepository.existsByEmail(newEmail)) {
                log.error("Email conflict: {}", newEmail);
                throw new DuplicateEntityException("Email already in use");
            }
            user.setEmail(newEmail);
            log.debug("Email updated for user: {}", user.getId());
        }
    }

    private void updatePhoneNumber(User user, String newPhoneNumber) {
        if (newPhoneNumber != null && !newPhoneNumber.equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(newPhoneNumber)) {
                log.error("Phone number conflict: {}", newPhoneNumber);
                throw new DuplicateEntityException("Phone number already in use");
            }
            user.setPhoneNumber(newPhoneNumber);
            log.debug("Phone number updated for user: {}", user.getId());
        }
    }

    public UserDTO findByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        return userRepository.findByUsernameIgnoreCase(username)
                .map(userMapper::toDto)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new NotFoundException("User not found");
                });
    }

    private void updateSocialLinks(User user, UserUpdateDTO dto) {
        if (dto.vkLink() != null) {
            user.setVkLink(dto.vkLink());
            log.debug("VK link updated for user: {}", user.getId());
        }
        if (dto.tgLink() != null) {
            user.setTgLink(dto.tgLink());
            log.debug("Telegram link updated for user: {}", user.getId());
        }
    }

    public List<UserDTO> searchUsers(String query) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AvatarUploadDTO uploadAvatar(Long userId, MultipartFile avatarFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<String> urls = fileStorageService.saveImages(List.of(avatarFile));
        if (urls.isEmpty()) {
            throw new RuntimeException("Avatar upload failed");
        }

        String avatarUrl = urls.get(0);

        Avatar avatar = new Avatar();
        avatar.setFilePath(avatarUrl);
        avatar.setUser(user);
        avatarRepository.save(avatar);

        user.setAvatar(avatar);
        userRepository.save(user);

        return avatarMapper.toDto(avatar);
    }

    public AvatarUploadDTO getAvatarByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Avatar avatar = user.getAvatar();

        String avatarUrl = (avatar != null) ? avatar.getFilePath() : null;

        AvatarUploadDTO dto = new AvatarUploadDTO();
        dto.setUserId(userId);
        dto.setAvatar(avatarUrl);

        return dto;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                AuthorityUtils.createAuthorityList(user.getRole())
        );
    }

    @Transactional
    public String generateTelegramToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        String token = UUID.randomUUID().toString();
        user.setTelegramLinkToken(token);
        user.setTelegramLinkTokenExpiration(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        log.info("Saving token: {}", token);
        return token;
    }

    public void linkTelegramChat(Long userId, String chatId) {
        System.out.println(">>> Linking telegram: userId=" + userId + ", chatId=" + chatId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setTelegramChatId(chatId);
        user.setTelegramLinkToken(null);
        userRepository.save(user);
        userRepository.flush();

        User saved = userRepository.findById(userId).orElseThrow();
        System.out.println(">>> After save: " + saved.getTelegramChatId() + " / " + saved.getTelegramLinkToken());
        System.out.println(">>> Saved user with chatId = " + user.getTelegramChatId());
    }

    public Optional<User> findByEmailAndPhone(String email, String phone) {
        return Optional.ofNullable(userRepository.findByEmailAndPhone(email, phone));
    }

    private void updateUserPassword(UserDTO userDTO, String newPassword) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    // UserService.java
    public boolean resetPasswordWithToken(String token, String newPassword) {
        // Проверка сложности пароля
        if (!isPasswordValid(newPassword)) {
            throw new IllegalArgumentException("Пароль не соответствует требованиям");
        }

        UserDTO userDTO = validateTelegramToken(token);
        if (userDTO == null) return false;

        updateUserPassword(userDTO, newPassword);

        // Инвалидация токена после использования
        invalidateTelegramToken(userDTO.getId());
        return true;
    }

    private boolean isPasswordValid(String password) {
        // Минимум 8 символов, цифры, буквы в верхнем и нижнем регистре
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        return password.matches(pattern);
    }

    @Transactional
    public void invalidateTelegramToken(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setTelegramLinkToken(null);
            userRepository.save(user);
        });
    }

    public UserDTO validateTelegramToken(String token) {
        log.debug(token);

        User user = userRepository.findByTelegramLinkToken(token)
                .orElseThrow(() -> new NotFoundException("Invalid token"));

        if (user.getTelegramLinkTokenExpiration() == null ||
                user.getTelegramLinkTokenExpiration().isBefore(LocalDateTime.now())) {
            log.warn("Token expired for user {}", user.getId());
            throw new NotFoundException("Token has expired");
        }

        return userMapper.toDto(user);
    }

}
