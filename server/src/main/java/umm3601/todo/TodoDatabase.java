package umm3601.todo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.BadRequestResponse;

/**
 * A fake "database" of todo info
 * <p>
 * Since we don't want to complicate this lab with a real database, we're going
 * to instead just read a bunch of user data from a specified JSON file, and
 * then provide various database-like methods that allow the `UserController` to
 * "query" the "database".
 */

public class TodoDatabase {

  public Todo getTodo(String id) {
    return Arrays.stream(allTodo).filter(x -> x._id.equals(id)).findFirst().orElse(null);
  }

  public static Todo[] limiting(Todo[] todos, int range) {
    return Arrays.stream(todos).limit(range).toArray(Todo[]::new);
  }

  private Todo[] allTodo;

  public TodoDatabase(String todoDataFile) throws IOException {
    InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(todoDataFile));
    ObjectMapper objectMapper = new ObjectMapper();
    allTodo = objectMapper.readValue(reader, Todo[].class);
  }

  public int size() {
    return allTodo.length;
  }


    public Todo[] listTodos(Map<String, List<String>> queryParams) {
      Todo[] filteredTodos = allTodo;

    // Filter Owner if defined
    if (queryParams.containsKey("owner")) {
      String targetOwner = queryParams.get("owner").get(0);
      filteredTodos = filterTodosByOwner(filteredTodos, targetOwner);
    }

    // Filter Category if defined
    if (queryParams.containsKey("category")) {
      String targetCategory = queryParams.get("category").get(0);
      filteredTodos = filterTodosByCategory(filteredTodos, targetCategory);
    }

    // Filter Contains if defined
    if (queryParams.containsKey("contains")) {
      String targetContains = queryParams.get("contains").get(0);
      filteredTodos = filterTodosByContains(filteredTodos, targetContains);
    }

    // Filter Status if defined
    if (queryParams.containsKey("status")) {
      String targetStatus = queryParams.get("status").get(0);
      filteredTodos = filterTodosByStatus(filteredTodos, targetStatus);
    }

    if (queryParams.containsKey("owner")) {
      String targetOwner = queryParams.get("owner").get(0);
      filteredTodos = filterTodosByOwner(filteredTodos, targetOwner);
    }

    if (queryParams.containsKey("orderBy")) {
      String targetSorting = queryParams.get("orderBy").get(0);
      filteredTodos = filterTodosBySorting(filteredTodos, targetSorting);
    }

    if (queryParams.containsKey("limit")) {
      String targetResult = queryParams.get("limit").get(0);
      try {
        int targetLimit = Integer.parseInt(targetResult);
        filteredTodos = limiting(filteredTodos, targetLimit);
      } catch (NumberFormatException e) {
        throw new BadRequestResponse("Specified limit '" + targetResult + "' can't be parsed to an integer");
      }
    }
    return filteredTodos;
  }

 /**
   * Get an array of all the users having the target company.
   *
   * @param todos         the list of users to filter by company
   * @param targetOwner  the target owner to look for
   * @return an array of all the users from the given list that have the target
   *         company
   */
  public Todo[] filterTodosByOwner(Todo[] todos, String targetOwner) {
    return Arrays.stream(todos).filter(x -> x.owner.equals(targetOwner)).toArray(Todo[]::new);
  }

  /**
   * Get an array of all the users having the target company.
   *    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("category", Arrays.asList(new String[] {"software design"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);
   * @param todos         the list of users to filter by company
   * @param targetCategory  the target category to look for
   * @return an array of all the users from the given list that have the target
   *         company
   */
  public Todo[] filterTodosByCategory(Todo[] todos, String targetCategory) {
    return Arrays.stream(todos).filter(x -> x.category.equals(targetCategory)).toArray(Todo[]::new);
  }
  /**
   * Get an array of all the users having the target company.
   *
   * @param todos         the list of users to filter by company
   * @param targetStatus  the target status to look for
   * @return an array of all the users from the given list that have the target
   */


  public Todo[] filterTodosByStatus(Todo[] todos, String targetStatus) {
    if (targetStatus.equals("incomplete")) {
      return Arrays.stream(todos).filter(x -> !x.status).toArray(Todo[]::new);
    } else {
      return Arrays.stream(todos).filter(x -> x.status).toArray(Todo[]::new);
    }
  }
/**
   * Get an array of all the users having the target company.
 * @param filteredTodos
   *
   * @param todos         the list of users to filter by company
   * @param targetContains  the target body to look for
   * @return an array of all the users from the given list that have the target
   */
  public Todo[] filterTodosByContains(Todo[] todos, String content) {
    return Arrays.stream(todos).filter(x -> x.body.contains(content)).toArray(Todo[]::new);
  }

/**
   * Get an array of all the users having the target company.
 * @param filteredSorting
   *
   * @param todos         the list of users to filter by company
   * @param targetContent1  the target content to look for
   * @return an array of all the users from the given list that have the target
   */

  public Todo[] filterTodosBySorting(Todo[] todos, String content1) {
    if (content1.equals("owner")) {
      Arrays.sort(todos, Comparator.comparing(Todo::getOwner));
      return todos;
    } else if (content1.equals("status")) {
      return Arrays.stream(todos).sorted(Comparator.comparing(Todo::getStatus)).toArray(Todo[]::new);
    } else if (content1.equals("body")) {
      return Arrays.stream(todos).sorted(Comparator.comparing(Todo::getBody)).toArray(Todo[]::new);
    } else if (content1.equals("category")) {
      return Arrays.stream(todos).sorted(Comparator.comparing(Todo::getCategory)).toArray(Todo[]::new);
    } else {
      throw new BadRequestResponse("nothing to sort");
    }
  }
}





