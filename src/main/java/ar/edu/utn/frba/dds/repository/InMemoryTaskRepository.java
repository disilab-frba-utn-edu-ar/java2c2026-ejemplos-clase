package ar.edu.utn.frba.dds.repository;

import static java.util.stream.Collectors.toList;

import ar.edu.utn.frba.dds.domain.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryTaskRepository implements TaskRepository {
  private final List<Task> tasks = new ArrayList<>();

  @Override
  public void save(Task task) {
    tasks.add(task);
  }

  @Override
  public List<Task> findAll() {
    return tasks.stream().filter(t -> !t.isDeleted()).collect(toList());
  }

  @Override
  public Optional<Task> findById(String id) {
    return tasks.stream().filter(t -> t.getId().equalsIgnoreCase(id)).findFirst();
  }
}
