package ar.edu.utn.frba.dds.service;

import ar.edu.utn.frba.dds.model.Todo;
import ar.edu.utn.frba.dds.repository.TodoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TodoService {
  private TodoRepository repository;

  public TodoService(TodoRepository repository) {
    this.repository = repository;
  }

  public List<Todo> getAllTodos() {
    return repository.findAll();
  }

  public Todo createTodo(Todo todo) {
    todo.setCompleted(false);
    return repository.save(todo);
  }
}
