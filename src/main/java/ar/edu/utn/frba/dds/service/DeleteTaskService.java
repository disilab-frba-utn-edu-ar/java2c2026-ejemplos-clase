package ar.edu.utn.frba.dds.service;

import ar.edu.utn.frba.dds.domain.Task;
import ar.edu.utn.frba.dds.repository.TaskRepository;

public class DeleteTaskService {
  private TaskRepository taskRepository;

  public DeleteTaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  public void markAsDeleted(String taskIdToBeDeleted) {
    taskRepository.findById(taskIdToBeDeleted).ifPresent(Task::markAsDeleted);
  }
}
