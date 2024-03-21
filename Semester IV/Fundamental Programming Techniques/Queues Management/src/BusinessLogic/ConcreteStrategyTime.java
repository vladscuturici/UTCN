package BusinessLogic;

import java.util.List;
import Model.Server;
import Model.Task;
public class ConcreteStrategyTime implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task t) {
        // Find the server with the minimum waiting time
        Server minServer = servers.get(0);
        for (Server s : servers) {
            //System.out.println(s.getWaitingPeriod() + minServer.getWaitingPeriod());
            if (s.getWaitingPeriod() < minServer.getWaitingPeriod()) {
                minServer = s;
            }
        }
        // Add the task to the server with the minimum waiting time
        minServer.addTask(t);
//        for (Model.Server s : servers) {
//            s.displayTasks();
//        }
        //System.out.println("Model.Task " + t + " has been added to server " + minServer.getId() + ", waiting time:" + minServer.getWaitingPeriod() + "\n");
       // minServer.displayTasks();
    }
}