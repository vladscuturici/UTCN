package BusinessLogic;

import Model.Server;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;
    private int clients;
    private SimulationManager simulationManager;
    public Scheduler(int maxNoServers, int maxTasksPerServer) {
        //for maxNoServers
        // - create server object
        // - create thread with the object
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        this.servers = new ArrayList<Server>();
        this.strategy = new ConcreteStrategyTime();
    }

    public void initServers(int n) {
        for(int i=0; i<n; i++) {
            Server s = new Server(i);
            servers.add(s);
            s.setSimulationManager(simulationManager);
            Thread t = new Thread(s);
            t.start();
        }
    }

    public void changeStrategy(SelectionPolicy policy) {
        //apply strategy pattern to instantiate the strategy
        //with concrete strategy corresponding to policy
        if (policy == SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new ConcreteStrategyQueue();
        }
        if (policy == SelectionPolicy.SHORTEST_TIME) {
            strategy = new ConcreteStrategyTime();
        }
    }

    public int getCount() {
        int c = 0;
        for(Server s : servers) {
            c += s.count();
        }
        return c;
    }

    public void setSimulationManager(SimulationManager simulationManager) {
        this.simulationManager = simulationManager;
    }

    public void dispatchTask(Model.Task t) {
        //call the strategy addTask method
        strategy.addTask(servers, t);
    }

    public List<Server> getServers() {
        return servers;
    }

    public void displayServers() {
        int id = 1;
        for(Server i : servers) {
            System.out.print("Queue " + id + ": ");
            Model.Task[] t = i.getTasks();
            int first = 1;
            for(int j=0; j < t.length; j++) {
                if(first == 0)
                    System.out.print(", ");
                else
                    first = 0;
                System.out.print(t[j]);
            }
            if(i.isEmpty())
                System.out.print("closed");
            id++;
            System.out.println('\n');
        }
    }
}