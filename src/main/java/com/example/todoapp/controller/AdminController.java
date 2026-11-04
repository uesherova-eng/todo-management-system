
package com.example.todoapp.controller;

import com.example.todoapp.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        return Map.of("totalUsers", userRepository.count());
    }

    @GetMapping("/users")
    public java.util.List<java.util.Map<String, Object>> getUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    java.util.Map<String, Object> item =
                            new java.util.LinkedHashMap<>();
                    item.put("id", user.getId());
                    item.put("username", user.getUsername());
                    item.put("createdAt", user.getCreatedAt());
                    return item;
                })
                .toList();
    }

}
