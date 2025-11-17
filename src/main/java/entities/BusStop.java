package entities;

public class BusStop {
    private int id;
    private int stopSequence;
    private String name;
    private Position position;
    private List<String> routes;

    public BusStop(int id, String name, Position position, List<String> routes) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.routes = routes;
    }

    // Getters and setters
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public List<String> getRoutes() {
        return routes;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "BusStop{" +
                "id='" + id + '\'' +
                ", name=" + name + '\n' +
                ", position=" + position + '\n'+
                ", stopSeq='" + stopSequence + '\'' +
                '}';
    }
}
