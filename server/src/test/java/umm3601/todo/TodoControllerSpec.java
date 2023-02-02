package umm3601.todo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import umm3601.Server;

/**
 * Tests the logic of the UserController
 *
 * @throws IOException
 */
// The tests here include a ton of "magic numbers" (numeric constants).
// It wasn't clear to me that giving all of them names would actually
// help things. The fact that it wasn't obvious what to call some
// of them says a lot. Maybe what this ultimately means is that
// these tests can/should be restructured so the constants (there are
// also a lot of "magic strings" that Checkstyle doesn't actually
// flag as a problem) make more sense.
@SuppressWarnings({ "MagicNumber" })
public class TodoControllerSpec {
  private Context ctx = mock(Context.class);
  private TodoController todoController;
  private static TodoDatabase db;

  @BeforeEach
  public void setUp() throws IOException {
    db = new TodoDatabase(Server.TODO_DATA_FILE);
    todoController = new TodoController(db);
  }

  @Test
  public void canGetAllUsers() throws IOException {
    todoController.getTodos(ctx);
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    assertEquals(db.size(), argument.getValue().length);
  }




  @Test
  public void canGetUsersWithOwner() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("owner", Arrays.asList(new String[] {"Fry"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    todoController.getTodos(ctx);

    // Confirm that all the users passed to `json` work for OHMNET.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    for (Todo todo : argument.getValue()) {
      assertEquals("Fry", todo.owner);
    }
  }



  @Test
  public void canGetTodosWithStatus() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("status", Arrays.asList(new String[] {"incomplete"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    todoController.getTodos(ctx);

    // Confirm that all the todos passed to `json` work for OHMNET.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    for (Todo todo : argument.getValue()) {
      assertEquals(false, todo.status);
    }
  }

  @Test
  public void canGetTodosWithStatusTestAnotherResult() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("status", Arrays.asList(new String[] {"complete"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    todoController.getTodos(ctx);

    // Confirm that all the todos passed to `json` work for OHMNET.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    for (Todo todo : argument.getValue()) {
      assertEquals(true, todo.status);
    }
  }


  @Test
  public void canGetTodoWithSpecifiedId() throws IOException {
    String id = "58895985a22c04e761776d54";
    Todo todo = db.getTodo(id);

    when(ctx.pathParam("id")).thenReturn(id);

    todoController.getTodo(ctx);

    verify(ctx).json(todo);
    verify(ctx).status(HttpStatus.OK);
    assertEquals("Blanche", todo.owner);
  }

  @Test
  public void respondsAppropriatelyToRequestForNonexistentId() throws IOException {
    when(ctx.pathParam("id")).thenReturn(null);
    Throwable exception = Assertions.assertThrows(NotFoundResponse.class, () -> {
      todoController.getTodo(ctx);
    });
    assertEquals("No todo with id " + null + " was found.", exception.getMessage());
  }


@Test
  public void canGetTodosWithCategory() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("category", Arrays.asList(new String[] {"software design"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    for (Todo todo : argument.getValue()) {
      assertEquals("software design", todo.category);
    }
  }
@Test
  public void filterByLimiting() {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("limit", Arrays.asList(new String[] {"qqq"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    Throwable exception = Assertions.assertThrows(BadRequestResponse.class, () -> {
      todoController.getTodos(ctx);
    });
    assertEquals(("Specified limit '" + "qqq" + "' can't be parsed to an integer"), exception.getMessage());
}
@Test
public void filterByContains() throws IOException {
  Map<String, List<String>> queryParams = new HashMap<>();
  queryParams.put("contains", Arrays.asList(new String[] {"ex"}));
  when(ctx.queryParamMap()).thenReturn(queryParams);
  todoController.getTodos(ctx);

  // Confirm that all the todos passed to `json` work for OHMNET.
  ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
  verify(ctx).json(argument.capture());
  for (Todo todo : argument.getValue()) {
    assertTrue(todo.body.contains("ex"));
  }
}

@Test
public void filterBySortingOwner() {
  Map<String, List<String>> queryParams = new HashMap<>();
  queryParams.put("orderBy", Arrays.asList(new String[] {"owner"}));
  when(ctx.queryParamMap()).thenReturn(queryParams);
  todoController.getTodos(ctx);
  ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
  verify(ctx).json(argument.capture());
  for (int i = 0; i<argument.getValue().length-1; i++ ){
    assertTrue(argument.getValue()[i].compareTo(argument.getValue()[i+1])<1);
  }
}

  @Test
  public void filterBySortingCategory() {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] {"category"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    for (int i = 0; i<argument.getValue().length-1; i++ ){
      assertTrue(argument.getValue()[i].compareTo(argument.getValue()[i+1])<1);
    }
  }

@Test
public void filterBySortingBody() {
  Map<String, List<String>> queryParams = new HashMap<>();
  queryParams.put("orderBy", Arrays.asList(new String[] {"body"}));
  when(ctx.queryParamMap()).thenReturn(queryParams);
  todoController.getTodos(ctx);
  ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
  verify(ctx).json(argument.capture());
  for (int i = 0; i<argument.getValue().length-1; i++ ){
    assertTrue(argument.getValue()[i].compareTo(argument.getValue()[i+1])<1);
  }
}
  @Test
  public void filterBySortingStatus() {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] {"status"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    for (int i = 0; i<argument.getValue().length-1; i++ ){
      assertTrue(argument.getValue()[i].compareTo(argument.getValue()[i+1])<1);
    }

}
}
