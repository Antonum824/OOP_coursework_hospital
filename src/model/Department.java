package model;

public class Department {
    private int id;
    private String name;
    private String headDoctor;
    private String phoneExtension;
    private int hospitalId;

    public Department(int id, String name, String headDoctor, String phoneExtension, int hospitalId) {
        this.id = id;
        this.name = name;
        this.headDoctor = headDoctor;
        this.phoneExtension = phoneExtension;
        this.hospitalId = hospitalId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getHeadDoctor() { return headDoctor; }
    public String getPhoneExtension() { return phoneExtension; }
    public int getHospitalId() { return hospitalId; }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}
