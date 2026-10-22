package ar.edu.utn.frba.dds.controller;

import ar.edu.utn.frba.dds.domain.Task;
import ar.edu.utn.frba.dds.service.AddTaskService;
import ar.edu.utn.frba.dds.service.CompleteTaskService;
import ar.edu.utn.frba.dds.service.DeleteTaskService;
import ar.edu.utn.frba.dds.service.ListTaskService;
import java.util.List;

public class TaskController {
  private AddTaskService addTaskService;
  private ListTaskService listTaskService;
  private CompleteTaskService completeTaskService;
  private DeleteTaskService deleteTaskService;

  public TaskController(AddTaskService addTaskService, ListTaskService listTaskService, CompleteTaskService completeTaskService, DeleteTaskService deleteTaskService) {
    this.addTaskService = addTaskService;
    this.listTaskService = listTaskService;
    this.completeTaskService = completeTaskService;
    this.deleteTaskService = deleteTaskService;
  }

  public Task addTask(String title) {
    return addTaskService.create(title);
  }

  public List<Task> listTasks() {
    return listTaskService.allTasks();
  }

  public void markAsCompleted(String id) {
    completeTaskService.maskAsComplete(id);
  }

  public void markAsDeleted(String taskIdToBeDeleted) {
    deleteTaskService.markAsDeleted(taskIdToBeDeleted);
  }
}
