package BusinessLogic;
    import Model.Task;
    import java.util.*;
    import java.io.FileWriter;
    import java.io.IOException;
    import GUI.SimulationFrame;
    import Model.Server;
    public class SimulationManager implements Runnable {
        public SimulationFrame frame;
        public int timeLimit = 1000;
        public int numberOfServers;
        public int numberOfClients = 100;
        public SelectionPolicy selectionPolicy;
        double averageWaitingTime;
        double averageServiceTime;
        private Scheduler scheduler;
        //private SimulationFrame frame;
        private List<Task> generatedTasks;
        private int peakHour;
        private int peakAmount = 0;
        private int totalWaitingTime = 0;
        public SimulationManager(SimulationFrame frame) {
            // initialize scheduler
            //	create and start numberOfServers
            //	initialize selection strategy => createStrategy
            // initialize frame to display simulation
            // generate numberOfClients clients using generateNRandomTasks()
            // and store them to generatedTasks;
            this.frame = frame;
            this.totalWaitingTime = 0;
            this.scheduler = new Scheduler(numberOfServers, 100);
            this.scheduler.setSimulationManager(this);
            this.scheduler.changeStrategy(SelectionPolicy.SHORTEST_TIME);
            this.generatedTasks = new ArrayList<Task>();
            this.selectionPolicy = SelectionPolicy.SHORTEST_TIME;
        }
        public void initServers() {
            for (int i = 0; i < numberOfServers; i++) {
                Server server = new Server(i);
                Thread t = new Thread(server);
                t.start();
            }
        }
         public void generateNRandomTasks(int n, int tMaxSim, int tMaxArrival, int tMinArrival, int tMaxService, int tMinService) {
            //generate N random tasks;
            //- random processing time;
            //minProcessingTime < processingTime < maxProcessingTime
            //random arrivalTime
            //sort list with respect to arrivalTime
            scheduler.initServers(numberOfServers);
            this.numberOfClients = n;
            this.timeLimit = tMaxSim;
            Random rand = new Random();
            for (int i = 1; i <= n; i++) {
                Task t = new Task(i, tMinArrival + rand.nextInt(tMaxArrival - tMinArrival + 1), tMinService + rand.nextInt(tMaxService - tMinService + 1));
                generatedTasks.add(t);
            }
            generatedTasks.sort(Comparator.comparingInt(Task::getArrivalTime));
        }
        public void addWaitingTime(int waitingTime) {
            //System.out.println(waitingTime);
            if(waitingTime > 0)
                this.totalWaitingTime += waitingTime;
            //System.out.println(this.totalWaitingTime);
        }
        void calculateAverageWaitingTime() {
            this.averageWaitingTime = (this.totalWaitingTime) / this.numberOfClients;
            if(this.averageWaitingTime < this.averageServiceTime)
                averageWaitingTime = averageServiceTime;
        }

        void calculateAverageServiceTime() {
            averageServiceTime = 0;
            for(Task t : generatedTasks) {
                averageServiceTime += t.getServiceTime();
            }
            averageServiceTime /= this.numberOfClients;
        }
        void displayRandomTasks() {
            for(Task j : generatedTasks) {
                System.out.println(j);
            }
        }

        public void printWaitingList(List<Task> tasks, FileWriter writer, SimulationFrame frame) throws IOException {
            int first = 1;
            writer.write("Waiting clients: ");
            frame.updateLogTextArea("Waiting clients: ");
            for (Task t : tasks) {
                if (first == 0) {
                    writer.write("; ");
                    frame.updateLogTextArea("; ");
                }
                else
                    first = 0;
                frame.updateLogTextArea(t.toString());
                writer.write(t.toString());
            }
            if(tasks.isEmpty()) {
                writer.write('-');
                frame.updateLogTextArea("-");
            }
            frame.updateLogTextArea("\n");
            writer.write('\n');
        }

        public void run() {
            int currentTime = 0;
            calculateAverageServiceTime();
            try {
                FileWriter writer = new FileWriter("logs.txt");
                while (currentTime <= timeLimit) {
                    writer.write("Time " + currentTime + '\n');
                    frame.updateLogTextArea("Time " + currentTime + '\n');
                    List<Server> servers = scheduler.getServers();
                    Iterator<Task> iterator = generatedTasks.iterator();
                    while (iterator.hasNext()) {
                        Task t = iterator.next();
                        if (t.getArrivalTime() == currentTime) {
                            scheduler.dispatchTask(t);
                            iterator.remove();
                        }
                    }
                    int id = 1, emptyQueues = 0;
                    emptyQueues = 1;
                    int count = scheduler.getCount();
                    if(peakAmount < count){
                        peakHour = currentTime;
                        peakAmount = count;
                    }
                    printWaitingList(generatedTasks, writer, frame);
                    for (Server i : servers) {
                        writer.write("Queue " + id + ": ");
                        frame.updateLogTextArea("Queue " + id + ": ");
                        id++;
                        i.displayTasks(writer, frame);
                        if (i.isEmpty()) {
                            writer.write("closed");
                            frame.updateLogTextArea("closed");
                        }
                        else
                            emptyQueues = 0;
                        writer.write('\n');
                        frame.updateLogTextArea("\n");
                        i.update();
                    }
                    currentTime++;
                    if (generatedTasks.isEmpty() && emptyQueues == 1)
                        break;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                calculateAverageWaitingTime();
                frame.updatePeakHourTextField(peakHour);
                frame.updateAvgServiceTimeTextField(averageServiceTime);
                frame.updateAvgWaitingTimeTextField(averageWaitingTime);
                writer.write("Average waiting time : " + averageWaitingTime + '\n');
                writer.write("Simulation finished.");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void startSimulation(int n, int q, int tMaxSim, int tMaxArrival, int tMinArrival, int tMaxService, int tMinService, SelectionPolicy s) {
            this.numberOfServers = q;
            generateNRandomTasks(n, tMaxSim, tMaxArrival, tMinArrival, tMaxService,tMinService);
            initServers();
            this.timeLimit = tMaxSim;
            scheduler.changeStrategy(s);
            Thread simulationThread = new Thread(this);
            simulationThread.start();
        }

        public static void main(String[] args) {
            new SimulationFrame();
        }
    }