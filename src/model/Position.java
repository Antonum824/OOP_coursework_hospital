package model;

public class Position {
    private int id;
    private String title;
    private double salary;

    public Position(int id, String title, double salary) {
        this.id = id;
        this.title = title;
        this.salary = salary;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public double getSalary() { return salary; }

    @Override
    public String toString() {
        return id + " - " + title;
    }
}
