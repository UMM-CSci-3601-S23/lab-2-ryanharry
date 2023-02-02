package umm3601.todo;

public class Todo {
  @SuppressWarnings({"MemberName"})
  public String _id;
  public String owner;
  public Boolean status;
  public String body;
  public String category;
  public Object sorting;
  public Object contains;

  public String getOwner() {
    return this.owner;
  }

  public Boolean getStatus() {
    return status;
  }

  public String getBody() {
    return body;
  }

  public String getCategory() {
    return category;
  }
}

