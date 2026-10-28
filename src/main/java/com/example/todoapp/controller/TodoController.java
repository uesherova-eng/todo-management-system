
package com.example.todoapp.controller;

import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import com.example.todoapp.repository.UserRepository;
import com.example.todoapp.model.User;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @GetMapping
    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Todo getTodoById(@PathVariable Long id) {
        return todoRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Todo createTodo(@RequestBody Todo todo) {

        if (todo.getUser() == null ||
                todo.getUser().getId() == null) {
            throw new IllegalArgumentException(
                    "User ID is required");
        }

        User user = userRepository.findById(
                todo.getUser().getId()).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException(
                    "User not found");
        }

        todo.setUser(user);
        todo.setCreatedAt(LocalDateTime.now());

        if (todo.getCompleted() == null) {
            todo.setCompleted(false);
        }

        return todoRepository.save(todo);
    }

    @PutMapping("/{id}")
    public Todo updateTodo(
            @PathVariable Long id,
            @RequestBody Todo updatedTodo) {

        Todo todo = todoRepository.findById(id).orElse(null);

        if (todo == null) {
            return null;
        }

        todo.setTitle(updatedTodo.getTitle());
        todo.setDescription(updatedTodo.getDescription());
        todo.setCompleted(updatedTodo.getCompleted());
        todo.setDueDate(updatedTodo.getDueDate());

        return todoRepository.save(todo);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        todoRepository.deleteById(id);
    }
}