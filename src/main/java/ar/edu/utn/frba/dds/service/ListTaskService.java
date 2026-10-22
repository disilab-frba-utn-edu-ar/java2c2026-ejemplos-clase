package ar.edu.utn.frba.dds.service;

import ar.edu.utn.frba.dds.domain.Task;
import ar.edu.utn.frba.dds.repository.TaskRepository;
import java.util.List;

public class ListTaskService {
  private TaskRepository taskRepository;

  public ListTaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  public List<Task> allTasks() {
    return taskRepository.findAll();
  }
}
