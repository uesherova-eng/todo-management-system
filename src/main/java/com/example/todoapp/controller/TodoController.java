
package com.example.todoapp.controller;

import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import com.example.todoapp.repository.UserRepository;
import com.example.todoapp.model.User;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoController(
            TodoRepository todoRepository,
            UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Необходимо войти в аккаунт"
            );
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Пользователь не найден"
                        ));
    }


    @GetMapping
    public List<Todo> getAllTodos(Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        return todoRepository.findByUserId(currentUser.getId());
    }



    @GetMapping("/{id}")
    public Todo getTodoById(
            @PathVariable Long id,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        return todoRepository.findByIdAndUserId(
                id,
                currentUser.getId()
        ).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Задача не найдена"
                ));
    }




    @PostMapping
    public Todo createTodo(
            @RequestBody Todo todo,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        todo.setId(null);
        todo.setUser(currentUser);
        todo.setCreatedAt(LocalDateTime.now());

        if (todo.getStatus() == null ||
                todo.getStatus().isBlank()) {
            todo.setStatus("To Do");
        }

        if (!todo.getStatus().equals("To Do")
                && !todo.getStatus().equals("In Progress")
                && !todo.getStatus().equals("Completed")) {
            throw new IllegalArgumentException("Invalid task status");
        }

        todo.setCompleted(
                todo.getStatus().equals("Completed")
        );

        if (todo.getPriority() == null ||
                todo.getPriority().isBlank()) {
            todo.setPriority("Medium");
        }

        if (!todo.getPriority().equals("High")
                && !todo.getPriority().equals("Medium")
                && !todo.getPriority().equals("Low")) {
            throw new IllegalArgumentException("Invalid task priority");
        }

        return todoRepository.save(todo);
    }


    @PutMapping("/{id}")
    public Todo updateTodo(
            @PathVariable Long id,
            @RequestBody Todo updatedTodo,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Todo todo = todoRepository.findByIdAndUserId(
                id,
                currentUser.getId()
        ).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Задача не найдена"
                ));


        todo.setTitle(updatedTodo.getTitle());
        todo.setDescription(updatedTodo.getDescription());
        String status = updatedTodo.getStatus();

        if (status != null && !status.isBlank()) {
            if (!status.equals("To Do")
                    && !status.equals("In Progress")
                    && !status.equals("Completed")) {
                throw new IllegalArgumentException("Invalid task status");
            }

            todo.setStatus(status);
            todo.setCompleted(status.equals("Completed"));
        } else if (updatedTodo.getCompleted() != null) {
            todo.setCompleted(updatedTodo.getCompleted());
            todo.setStatus(updatedTodo.getCompleted() ? "Completed" : "To Do");
        }
        todo.setDueDate(updatedTodo.getDueDate());

        if (updatedTodo.getPriority() != null
                && !updatedTodo.getPriority().isBlank()) {

            String priority = updatedTodo.getPriority();

            if (!priority.equals("High")
                    && !priority.equals("Medium")
                    && !priority.equals("Low")) {
                throw new IllegalArgumentException("Invalid task priority");
            }

            todo.setPriority(priority);
        }


        return todoRepository.save(todo);
    }


    @DeleteMapping("/{id}")
    public void deleteTodo(
            @PathVariable Long id,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Todo todo = todoRepository.findByIdAndUserId(
                id,
                currentUser.getId()
        ).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Задача не найдена"
                ));

        todoRepository.delete(todo);
    }

}