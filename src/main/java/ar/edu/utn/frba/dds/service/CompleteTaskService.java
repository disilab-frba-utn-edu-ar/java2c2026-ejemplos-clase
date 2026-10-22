package ar.edu.utn.frba.dds.service;

import ar.edu.utn.frba.dds.domain.Task;
import ar.edu.utn.frba.dds.repository.InMemoryTaskRepository;
import ar.edu.utn.frba.dds.repository.TaskRepository;
import java.util.Optional;

public class CompleteTaskService {

  private TaskRepository taskRepository;

  public CompleteTaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  public void maskAsComplete(String id) {
    Optional<Task> task = taskRepository.findById(id);
    task.ifPresent(t -> {if (!t.isDeleted()) {
      t.markAsCompleted();}
    });
  }
}
