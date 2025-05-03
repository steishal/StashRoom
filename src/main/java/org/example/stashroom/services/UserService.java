package org.example.stashroom.services;
import lombok.extern.slf4j.Slf4j;
import org.example.stashroom.dto.AuthDTO;
import org.example.stashroom.dto.UserCreateDTO;
import org.example.stashroom.dto.UserDTO;
import org.example.stashroom.dto.UserUpdateDTO;
import org.example.stashroom.entities.User;
import org.example.stashroom.exceptions.DuplicateEntityException;
import org.example.stashroom.exceptions.InvalidCredentialsException;
import org.example.stashroom.exceptions.NotFoundException;
import org.example.stashroom.mappers.UserMapper;
import org.example.stashroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO findById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        return userRepository.findById(id)
                .map(user -> {
                    log.info("Found user: {}", user.getUsername());
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
        log.debug("Authenticating user: {}", dto.username());
        User user = userRepository.findByUsernameIgnoreCase(dto.username())
                .orElseThrow(() -> {
                    log.error("Authentication failed for user: {}", dto.username());
                    return new InvalidCredentialsException();
                });

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            log.error("Invalid password for user: {}", dto.username());
            throw new InvalidCredentialsException();
        }

        log.info("User authenticated: {}", dto.username());
        return userMapper.toDto(user);
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
}
