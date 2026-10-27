package ar.edu.utn.frba.dds.unit;

import ar.edu.utn.frba.dds.Todo;
import ar.edu.utn.frba.dds.TodoRepository;
import ar.edu.utn.frba.dds.TodoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private Todo sampleTodo;

    @BeforeEach
    void setUp() {
        sampleTodo = new Todo(1L, "Aprender Unit Tests", false);
    }

    @Test
    void findAll_ShouldReturnTodoList_WhenTodosExist() {
        // Arrange
        List<Todo> expectedTodos = Arrays.asList(
            sampleTodo,
            new Todo(2L, "Hacer la compra", true)
        );
        when(todoRepository.findAll()).thenReturn(expectedTodos);

        // Act
        List<Todo> actualTodos = todoService.findAll();

        // Assert
        assertNotNull(actualTodos);
        assertEquals(2, actualTodos.size());
        assertEquals(expectedTodos, actualTodos);
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoTodosExist() {
        // Arrange
        when(todoRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Todo> actualTodos = todoService.findAll();

        // Assert
        assertNotNull(actualTodos);
        assertTrue(actualTodos.isEmpty());
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void save_ShouldReturnSavedTodo_WhenValidTodoIsProvided() {
        // Arrange
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        // Act
        Todo savedTodo = todoService.save(sampleTodo);

        // Assert
        assertNotNull(savedTodo);
        assertEquals(sampleTodo.getId(), savedTodo.getId());
        assertEquals(sampleTodo.getTitle(), savedTodo.getTitle());
        verify(todoRepository, times(1)).save(sampleTodo);
    }
}