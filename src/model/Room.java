package model;

public class Room {
    private int id;
    private int roomNumber;
    private int departmentId;
    private int beds;
    private boolean isFree;

    public Room(int id, int roomNumber, int departmentId, int beds, boolean isFree) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.departmentId = departmentId;
        this.beds = beds;
        this.isFree = isFree;
    }

    public int getId() { return id; }
    public int getRoomNumber() { return roomNumber; }
    public int getDepartmentId() { return departmentId; }
    public int getBeds() { return beds; }
    public boolean isFree() { return isFree; }

    @Override
    public String toString() {
        return roomNumber + " (" + beds + " мест)";
    }
}
