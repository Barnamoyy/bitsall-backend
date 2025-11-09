package com.bitsall.service;

import com.bitsall.model.dto.UserRequest;
import com.bitsall.model.dto.UserResponse;
import com.bitsall.model.entity.User;
import com.bitsall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserRequest userRequest) {
        // Check if email already exists
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + userRequest.getEmail() + " already exists");
        }

        User user = User.builder()
                .fullName(userRequest.getFullName())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .role(userRequest.getRole())
                .departments(userRequest.getDepartments())
                .clubs(userRequest.getClubs())
                .phoneNumber(userRequest.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);
        return toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .departments(user.getDepartments())
                .clubs(user.getClubs())
                .phoneNumber(user.getPhoneNumber())
                .dateJoined(user.getDateJoined())
                .verified(user.isVerified())
                .build();
    }

    @Transactional(readOnly = true)
    public User getUserEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
