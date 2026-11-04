
package com.example.todoapp.controller;

import com.example.todoapp.dto.RegisterRequest;
import com.example.todoapp.dto.LoginRequest;
import com.example.todoapp.model.User;
import com.example.todoapp.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        if (request.getUsername() == null ||
                request.getUsername().isBlank() ||
                request.getEmail() == null ||
                request.getEmail().isBlank() ||
                request.getPassword() == null ||
                request.getPassword().length() < 12) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Укажи имя, email и пароль от 12 символов"));
        }

        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email) ||
                userRepository.existsByUsername(username)) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message",
                            "Email или имя пользователя уже заняты"));
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message",
                        "Регистрация прошла успешно"));


    }


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        if (request.getEmail() == null ||
                request.getPassword() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message", "Введите email и пароль"));
        }

        String email = request.getEmail().trim().toLowerCase();

        var userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty() ||
                !passwordEncoder.matches(
                        request.getPassword(),
                        userOptional.get().getPasswordHash())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message", "Неверный email или пароль"));
        }

        User user = userOptional.get();

        Authentication authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        user.getEmail(),
                        null,
                        List.of(new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole()
                        )));
        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        SecurityContextRepository repository =
                new HttpSessionSecurityContextRepository();

        httpRequest.getSession(true);
        httpRequest.changeSessionId();

        repository.saveContext(
                context,
                httpRequest,
                httpResponse);

        return ResponseEntity.ok(Map.of(
                "message", "Вход выполнен успешно",
                "username", user.getUsername()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(Map.of(
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "createdAt", user.getCreatedAt().toString()
                )))
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                );
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            Authentication authentication,
            @RequestBody Map<String, String> request) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String newUsername = request.get("username");

        if (newUsername == null || newUsername.isBlank()
                || newUsername.trim().length() > 100) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Имя должно содержать от 1 до 100 символов"));
        }

        newUsername = newUsername.trim();

        var userOptional = userRepository.findByEmail(authentication.getName());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User user = userOptional.get();

        if (!user.getUsername().equals(newUsername)
                && userRepository.existsByUsername(newUsername)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Это имя уже занято"));
        }

        user.setUsername(newUsername);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Имя успешно изменено",
                "username", user.getUsername()
        ));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            Authentication authentication,
            @RequestBody Map<String, String> request) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (currentPassword == null || newPassword == null
                || newPassword.length() < 12) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Новый пароль должен содержать минимум 12 символов"));
        }

        var userOptional = userRepository.findByEmail(authentication.getName());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Неверный текущий пароль"));
        }

        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Новый пароль должен отличаться от текущего"));
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Пароль успешно изменён"
        ));
    }


}
