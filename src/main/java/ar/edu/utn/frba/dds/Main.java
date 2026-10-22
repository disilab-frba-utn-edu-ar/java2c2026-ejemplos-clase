package ar.edu.utn.frba.dds;

import ar.edu.utn.frba.dds.cli.ConsoleUI;
import ar.edu.utn.frba.dds.controller.TaskController;
import ar.edu.utn.frba.dds.repository.InMemoryTaskRepository;
import ar.edu.utn.frba.dds.repository.TaskRepository;
import ar.edu.utn.frba.dds.service.AddTaskService;
import ar.edu.utn.frba.dds.service.CompleteTaskService;
import ar.edu.utn.frba.dds.service.DeleteTaskService;
import ar.edu.utn.frba.dds.service.ListTaskService;

public class Main {

  public static void main(String[] args) {
    TaskRepository taskRepository = new InMemoryTaskRepository();;
    TaskController controller = new TaskController(new AddTaskService(taskRepository),
        new ListTaskService(taskRepository), new CompleteTaskService(taskRepository), new DeleteTaskService(taskRepository));

    new ConsoleUI(controller).start();
  }

}