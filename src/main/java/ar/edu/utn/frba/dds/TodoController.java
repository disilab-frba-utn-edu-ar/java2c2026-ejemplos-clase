package ar.edu.utn.frba.dds;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@Tag(name = "Todos", description = "Endpoints para la gestión de tareas pendientes")
public class TodoController {

    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);
    
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las tareas")
    public ResponseEntity<List<Todo>> getAllTodos() {
        logger.info("¡Hola! Se ha consultado el endpoint de prueba de la To-Do List.");
        return ResponseEntity.ok(todoService.findAll());
    }

    @PostMapping
    @Operation(summary = "Crear una nueva tarea")
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
        Todo savedTodo = todoService.save(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTodo);
    }
}
