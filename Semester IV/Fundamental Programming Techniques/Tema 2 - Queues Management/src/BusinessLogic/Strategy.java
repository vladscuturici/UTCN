package BusinessLogic;
import java.util.List;

public interface Strategy {
    public void addTask(List<Model.Server> servers, Model.Task t);
}
