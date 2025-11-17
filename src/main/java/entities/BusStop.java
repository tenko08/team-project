package entities;

public class BusStop {
    private int id;
    private int stopSequence;
    private String name;
    private Position position;

    public  BusStop(int id, int stopSequence, String name, Position position) {
        this.id = id;
        this.stopSequence = stopSequence;
        this.name = name;
        this.position = position;
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public int getStopSequence() {
        return stopSequence;
    }
    
}
