package ar.edu.utn.frba.dds.controller;

import ar.edu.utn.frba.dds.model.Todo;
import ar.edu.utn.frba.dds.service.TodoService;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {
  private TodoService service;

  public TodoController(TodoService service) {
    this.service = service;
  }

  @GetMapping
  public List<Todo> getAll() {
    return service.getAllTodos();
  }

  @PostMapping
  public Todo create(@RequestBody Todo todo) {
    return service.createTodo(todo);
  }

}
