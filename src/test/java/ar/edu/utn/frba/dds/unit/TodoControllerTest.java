package ar.edu.utn.frba.dds.unit;

import ar.edu.utn.frba.dds.Todo;
import ar.edu.utn.frba.dds.TodoController;
import ar.edu.utn.frba.dds.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TodoService todoService;

  @Autowired
  private ObjectMapper objectMapper;

  private Todo sampleTodo;

  @BeforeEach
  void setUp() {
    sampleTodo = new Todo(1L, "Aprender Unit Tests", false);
  }

  @Test
  void getAllTodos_ShouldReturnOkAndListOfTodos() throws Exception {
    // Arrange
    List<Todo> todos = Arrays.asList(sampleTodo, new Todo(2L, "Hacer la compra", true));
    when(todoService.findAll()).thenReturn(todos);

    // Act & Assert
    mockMvc.perform(get("/api/todos"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].title").value("Aprender Unit Tests"))
        .andExpect(jsonPath("$[0].completed").value(false))
        .andExpect(jsonPath("$[1].id").value(2L));
  }

  @Test
  void getAllTodos_ShouldReturnOkAndEmptyList_WhenNoTodosExist() throws Exception {
    // Arrange
    when(todoService.findAll()).thenReturn(Collections.emptyList());

    // Act & Assert
    mockMvc.perform(get("/api/todos"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(0));
  }

  @Test
  void createTodo_ShouldReturnCreatedStatusAndTodo() throws Exception {
    // Arrange
    when(todoService.save(any(Todo.class))).thenReturn(sampleTodo);

    // Act & Assert
    mockMvc.perform(post("/api/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sampleTodo)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.title").value("Aprender Unit Tests"))
        .andExpect(jsonPath("$.completed").value(false));
  }
}