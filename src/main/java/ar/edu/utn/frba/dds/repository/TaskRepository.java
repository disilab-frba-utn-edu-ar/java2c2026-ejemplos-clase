package ar.edu.utn.frba.dds.repository;

import ar.edu.utn.frba.dds.domain.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
  void save(Task task);
  List<Task> findAll();

  Optional<Task> findById(String id);
}
