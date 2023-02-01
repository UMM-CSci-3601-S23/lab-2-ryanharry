package umm3601.todo;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;


//TodoController that manages requests for info about todo.

public class TodoController {

  private TodoDatabase database;

  public TodoController(TodoDatabase database) {
    this.database = database;
  }

  public void getTodo(Context ctx1) {
    String id = ctx1.pathParam("id");
    Todo todo = database.getTodo(id);
    if (todo != null) {
      ctx1.json(todo);
      ctx1.status(HttpStatus.OK);
    } else {
      throw new NotFoundResponse("No todo with id " + id + " was found.");
    }
  }

  public void getTodos(Context ctx1) {
    Todo[] todos = database.listTodos(ctx1.queryParamMap());
    ctx1.json(todos);
  }

}

