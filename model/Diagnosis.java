package model;

public class Diagnosis {
    private int id;
    private String name;
    private String icdCode;
    private String description;

    public Diagnosis(int id, String name, String icdCode, String description) {
        this.id = id;
        this.name = name;
        this.icdCode = icdCode;
        this.description = description;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getIcdCode() { return icdCode; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}
