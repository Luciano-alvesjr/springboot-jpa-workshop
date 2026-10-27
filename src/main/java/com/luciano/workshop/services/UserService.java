package com.luciano.workshop.services;

import com.luciano.workshop.dto.*;
import com.luciano.workshop.entities.User;
import com.luciano.workshop.repositories.UserRepository;
import com.luciano.workshop.services.exceptions.*;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwords;

    public UserService(UserRepository repository, PasswordEncoder passwords) {
        this.repository = repository;
        this.passwords = passwords;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return repository.findAll(Sort.by("id")).stream().map(UserResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) { return UserResponse.from(requireUser(id)); }

    @Transactional
    public UserResponse insert(UserCreateRequest request) {
        String email = normalizeEmail(request.email());
        if (repository.existsByEmail(email)) throw new DatabaseException("Email is already registered");
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Password must contain at most 72 UTF-8 bytes");
        }
        User user = new User(request.name().strip(), email, request.phone().strip(), passwords.encode(request.password()));
        return UserResponse.from(repository.saveAndFlush(user));
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = requireUser(id);
        String email = normalizeEmail(request.email());
        if (!email.equals(user.getEmail()) && repository.existsByEmail(email)) {
            throw new DatabaseException("Email is already registered");
        }
        user.setName(request.name().strip());
        user.setEmail(email);
        user.setPhone(request.phone().strip());
        return UserResponse.from(repository.saveAndFlush(user));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(requireUser(id));
        repository.flush();
    }

    private User requireUser(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
    private String normalizeEmail(String email) { return email.strip().toLowerCase(Locale.ROOT); }
}
