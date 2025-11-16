package entities;

public class BusStop {
    private int id;
    private int stopSequence;

    public  BusStop(int id, int stopSequence) {
        this.id = id;
        this.stopSequence = stopSequence;
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public int getStopSequence() {
        return stopSequence;
    }
    
}
