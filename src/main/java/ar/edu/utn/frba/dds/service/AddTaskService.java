package ar.edu.utn.frba.dds.service;

import static java.util.UUID.randomUUID;

import ar.edu.utn.frba.dds.domain.Task;
import ar.edu.utn.frba.dds.repository.TaskRepository;
import java.util.UUID;

public class AddTaskService {
  private TaskRepository taskRepository;

  public AddTaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  public Task create(String title) {
    if (title == null || title.trim().isEmpty()) {
      throw new IllegalArgumentException("El título no puede ser vacío.");
    }
    Task task = new Task(randomUUID().toString().substring(0, 6), title);
    taskRepository.save(task);
    return task;
  }
}
