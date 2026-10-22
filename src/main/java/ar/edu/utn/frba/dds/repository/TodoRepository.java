package ar.edu.utn.frba.dds.repository;

import ar.edu.utn.frba.dds.model.Todo;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class TodoRepository {
  private List<Todo> todos = new ArrayList<>();
  private AtomicLong counter = new AtomicLong(1);

  public List<Todo> findAll() {
    return todos;
  }

  public Todo save(Todo todo) {
    if (todo.getId() == null) {
      todo.setId(counter.getAndIncrement());
      todos.add(todo);
    }
    return todo;
  }
}
