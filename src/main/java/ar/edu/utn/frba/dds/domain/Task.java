package ar.edu.utn.frba.dds.domain;

public class Task {
  private String id;
  private String title;
  private boolean completed;
  private boolean deleted;

  public Task(String id, String title) {
    this.id = id;
    this.title = title;
    this.completed = false;
    this.deleted = false;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void markAsCompleted() {
    completed = true;
  }

  public boolean isCompleted() {
    return completed;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void markAsDeleted() {
    deleted = true;
  }
}
