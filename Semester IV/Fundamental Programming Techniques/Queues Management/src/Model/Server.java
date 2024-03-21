package Model;

import BusinessLogic.SimulationManager;
import GUI.SimulationFrame;

import java.io.FileWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private int id;
    private SimulationManager simulationManager;
    public Server(int id) {
        //initialize queue and waiting period
        this.id = id;
        tasks = new LinkedBlockingQueue<>();
        waitingPeriod = new AtomicInteger(0);
    }

    public void setSimulationManager(SimulationManager simulationManager) {
        this.simulationManager = simulationManager;
    }

    public void addTask(Task newTask) {
        //add task to queue
        //increment the waitingPeriod
        tasks.add(newTask);
        waitingPeriod.addAndGet(newTask.getServiceTime());
    }

    public void run() {
        while (true) {
            try {
                // Take task from queue
                // Stop the thread for the duration of the task's processing time
                // Decrement the waitingPeriod
                if(!tasks.isEmpty()) {
                    Task t = tasks.peek();
                    if(t.getServiceTime() >=0)
                        Thread.sleep(t.getServiceTime() * 1000);
                    waitingPeriod.addAndGet(-t.getServiceTime());
                    //System.out.println(t.getWaitingTime());
                    simulationManager.addWaitingTime(t.getWaitingTime());
                    t = tasks.take();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void update() {
        if (tasks.peek() != null) {
            tasks.peek().decrementServiceTime();
        }
        for(Task t : tasks) {
            t.incrementWaitingTime();
        }
    }

    public int count() {
        return tasks.size();
    }
    public int getWaitingPeriod () {
        return waitingPeriod.get();
    }

    public Task[] getTasks() {
        Task[] taskArray = new Task[tasks.size()];
        tasks.toArray(taskArray);
        return taskArray;
    }

    public boolean isEmpty() {
        if(tasks.isEmpty())
            return true;
        return false;
    }

    public void displayTasks(FileWriter writer, SimulationFrame frame) {
        int first = 1;
        try {
            for (Task t : tasks) {
                if(t.getServiceTime() != 0) {
                    if (first == 1)
                        first = 0;
                    else {
                        writer.write(", ");
                        frame.updateLogTextArea(", ");
                    }
                    writer.write(t.toString());
                    frame.updateLogTextArea(t.toString());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getId() {
        return this.id;
    }
}