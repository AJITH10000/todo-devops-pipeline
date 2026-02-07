package com.todoapp.controller;

import com.todoapp.entity.Todo;
import com.todoapp.service.CohereService;
import com.todoapp.service.SlackService;
import com.todoapp.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private CohereService cohereService;

    @Autowired
    private SlackService slackService;

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        return ResponseEntity.ok(todoService.getAllTodos());
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
        return new ResponseEntity<>(todoService.createTodo(todo), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo todoDetails) {
        return ResponseEntity.ok(todoService.updateTodo(id, todoDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/summarize")
    public ResponseEntity<String> summarizeAndSendToSlack() {
        try {
            List<Todo> pendingTodos = todoService.getPendingTodos();

            if (pendingTodos.isEmpty()) {
                return ResponseEntity.ok("No pending todos to summarize.");
            }

            String todosText = pendingTodos.stream()
                    .map(todo -> "- " + todo.getTitle() + ": " + todo.getDescription())
                    .collect(Collectors.joining("\n"));

            String summary = cohereService.summarizeText("Summarize these todos:\n" + todosText);

            slackService.sendSlackMessage("Todo Summary:\n" + summary);

            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating summary: " + e.getMessage());
        }
    }
}
