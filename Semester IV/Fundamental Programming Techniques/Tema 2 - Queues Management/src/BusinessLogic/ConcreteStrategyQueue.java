package BusinessLogic;
import Model.Server;
import Model.Task;
import java.util.List;

public class ConcreteStrategyQueue implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task t) {
        // Find the server with the shortest queue
        Server minServer = servers.get(0);
        for (Server s : servers) {
            if (s.count() < minServer.count()) {
                minServer = s;
            }
        }
        minServer.addTask(t);
    }
}