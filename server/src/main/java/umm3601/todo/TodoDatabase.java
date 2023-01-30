package umm3601.todo;
import java.io.IOException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.http.BadRequestResponse;

import java.util.stream.Stream;

//import io.javalin.http.BadRequestResponse;

/**
 * A fake "database" of user info
 * <p>
 * Since we don't want to complicate this lab with a real database, we're going
 * to instead just read a bunch of user data from a specified JSON file, and
 * then provide various database-like methods that allow the `UserController` to
 * "query" the "database".
 */

public class TodoDatabase {


  public static Todo[] limiting_fun(Todo[] todos, int range){
    return Arrays.stream(todos).limit(range).toArray(Todo[]::new);
  }


  /*public static Todo[] FilterByContains(Todo[] todos, String content){
    return Arrays.stream(todos).limit(range).toArray(Todo[]::new);
  }*/


  private Todo[] allTodo;

  public TodoDatabase(String todoDataFile) throws IOException {
    InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(todoDataFile));
    ObjectMapper objectMapper = new ObjectMapper();
    allTodo = objectMapper.readValue(reader, Todo[].class);
  }

  public int size() {
    return allTodo.length;
  }

  /**
   * Get the single user specified by the given ID. Return `null` if there is no
   * user with that ID.
   *
   * @param id the ID of the desired user
   * @return the user with the given ID, or null if there is no user with that ID
   */
  public Todo getTodo(String id) {
    return Arrays.stream(allTodo).filter(x -> x._id.equals(id)).findFirst().orElse(null);
  }

  /**
   * Get an array of all the users satisfying the queries in the params.
   *
   * @param queryParams map of key-value pairs for the query
   * @return an array of all the users matching the given criteria
   */
  public Todo[] listTodos(Map<String, List<String>> queryParams) {
    Todo[] filteredTodos = allTodo;
    /*
    // Filter age if defined
    if (queryParams.containsKey("age"))("age").get(0);
      try {
        int targetAge = Integer.parseInt(ageParam);
        filteredTodos = filterTodosByAge(filteredTodos, targetAge);
      } catch (NumberFormatException e) {
        throw new BadRequestResponse("Specified age '" + ageParam + "' can't be parsed to an integer");
      }
    */

    // Filter company if defined
    if (queryParams.containsKey("owner")) {
      String targetOwner = queryParams.get("owner").get(0);
      filteredTodos = filterTodosByOwner(filteredTodos, targetOwner);
    }

    // Process other query parameters here...
    if (queryParams.containsKey("category")){
      String targetCategory = queryParams.get("category").get(0);
      filteredTodos = filterTodosByCategory(filteredTodos, targetCategory);
    }

    if (queryParams.containsKey("contains")){
      String targetBody = queryParams.get("contains").get(0);
    }

    if (queryParams.containsKey("status")){
      String targetStatus= queryParams.get("status").get(0);
      filteredTodos = filterTodosByStatus(filteredTodos, targetStatus);
    }

    if (queryParams.containsKey("owner")) {
      String targetOwner = queryParams.get("owner").get(0);
      filteredTodos = filterTodosByOwner(filteredTodos, targetOwner);}

    if (queryParams.containsKey("limit")) {
      String targetResult = queryParams.get("limit").get(0);
      try {
        int targetLimit = Integer.parseInt(targetResult);
        filteredTodos = limiting_fun(filteredTodos, targetLimit);
      } catch (NumberFormatException e) {
        throw new BadRequestResponse("Specified limit '" + targetResult+ "' can't be parsed to an integer");
      }

    }
    return filteredTodos;
  }


  /**
   * Get an array of all the users having the target owner.
   *miting_fiven list that have the target
   *         age
   */
  public Todo[] filterTodosByOwner(Todo[] todos, String targetOwner) {
    return Arrays.stream(todos).filter(x -> x.owner.equals(targetOwner)).toArray(Todo[]::new);
  }

  /**
   * Get an array of all the users having the target company.
   *
   * @param todos         the list of users to filter by company
   * @param targetCategory  the target company to look for
   * @return an array of all the users from the given list that have the target
   *         company
   */
  public Todo[] filterTodosByCategory(Todo[] todos, String targetCategory){
    return Arrays.stream(todos).filter(x -> x.category.equals(targetCategory)).toArray(Todo[]::new);
  }
  /**
   * Get an array of all the users having the target company.
   *
   * @param todos         the list of users to filter by company
   * @param targetStatus  the target company to look for
   * @return an array of all the users from the given list that have the target
   */


  public Todo[] filterTodosByStatus(Todo[] todos, String targetStatus){
    if(targetStatus.equals("incomplete")){
      return Arrays.stream(todos).filter(x -> x.status == false).toArray(Todo[]::new);
    }
    else{
      return Arrays.stream(todos).filter(x -> x.status == true).toArray(Todo[]::new);
    }
  }
/**
   * Get an array of all the users having the target company.
   *
   * @param todos         the list of users to filter by company
   * @param targetBody  the target company to look for
   * @return an array of all the users from the given list that have the target
   */
  public Todo[] filterTodosByBody(Todo[] todos, String targetBody){
    return Arrays.stream(todos).filter(x -> x.category.equals(targetBody)).toArray(Todo[]::new);
  }
}





