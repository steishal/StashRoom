package org.example.stashroom.services;
import org.example.stashroom.dto.AuthDTO;
import org.example.stashroom.dto.UserCreateDTO;
import org.example.stashroom.dto.UserDTO;
import org.example.stashroom.entities.User;
import org.example.stashroom.mappers.UserMapper;
import org.example.stashroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {
    @Autowired private UserRepository userRepository;
    @Autowired private UserMapper userMapper;

    public UserDTO findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public UserDTO register(UserCreateDTO dto) {
        User user = userMapper.fromCreateDto(dto);
        // TODO: encode password, set default role
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    public UserDTO authenticate(AuthDTO dto) {
        // TODO: implement authentication logic (compare password hashes, etc.)
        User user = userRepository.findByUsernameIgnoreCase(dto.username())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        // assume password matches
        return userMapper.toDto(user);
    }
}
