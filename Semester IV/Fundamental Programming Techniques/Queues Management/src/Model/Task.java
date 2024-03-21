package Model;

public class Task {
    private int id;
    private int arrivalTime;
    private int serviceTime;
    private int waitingTime;
    public Task(int id, int arrivalTime, int serviceTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }
    void decrementServiceTime() {
        this.serviceTime--;
    }
    public void incrementWaitingTime() {
        this.waitingTime++;
    }
    public int getWaitingTime() {
        return waitingTime;
    }
    public int getServiceTime() {
        return serviceTime;
    }
    public int getArrivalTime() {
        return arrivalTime;
    }
    public int getProcessingTime() {
        return (serviceTime) * 1000;
    }
    public String toString() {
        return "("+ id + ", " + arrivalTime + ", " + serviceTime +")";
    }
}
