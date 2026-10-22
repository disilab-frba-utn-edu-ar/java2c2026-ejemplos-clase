package ar.edu.utn.frba.dds.cli;

import static java.lang.System.in;

import ar.edu.utn.frba.dds.controller.TaskController;
import ar.edu.utn.frba.dds.domain.Task;
import java.util.Scanner;

public class ConsoleUI {
  private TaskController controller;
  private final Scanner scanner;

  public ConsoleUI(TaskController controller) {
    this.scanner = new Scanner(in);
    this.controller = controller;
  }

  public void start() {
    boolean running = true;
    while (running) {
      String option = printMenu();

      switch (option) {
        case "1":
          System.out.print("Título de la tarea: ");
          String title = scanner.nextLine();

          Task newTask = controller.addTask(title);
          System.out.printf("\n✅ Tarea creada: \"%s\" (ID: %s)%n", newTask.getTitle(), newTask.getId());
          break;
        case "2":
          System.out.println("\n--- LISTA DE TAREAS ---");
          if (controller.listTasks().isEmpty()) {
            System.out.println("No hay tareas.");
            break;
          }
          for (Task t : controller.listTasks()) {
            String status = t.isCompleted() ? "X" : " ";
            System.out.printf("[%s] %s - ID: %s", status, t.getTitle(), t.getId());
          }
          break;
        case "3":
          System.out.print("ID de la tarea a ser marcada como completa: ");
          String taskIdToBeMarkAsCompleted = scanner.nextLine();
          controller.markAsCompleted(taskIdToBeMarkAsCompleted);
          break;
        case "4":
          System.out.print("ID de la tarea a eliminar: ");
          String taskIdToBeDeleted = scanner.nextLine();
          controller.markAsDeleted(taskIdToBeDeleted);
          break;
        case "5":
          System.out.println("\n¡Adiós!");
          running = false;
          break;
        default:
          System.out.println("\n❌ Opción no válida.");
      }
    }
    scanner.close();
  }

  private String printMenu() {
    System.out.println("\n--- GESTOR DE TAREAS CLI ---");
    System.out.println("\n1. Crear Tarea\n2. Listar Tareas\n3. Marcar como completada\n4. Borrar tarea\n5. Salir");
    System.out.print("\nSelecciona una opción: ");
    String option = scanner.nextLine().trim();
    return option;
  }
}
