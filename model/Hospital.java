package model;

public class Hospital {
    private int id;
    private String name;
    private String address;
    private String phone;
    private String inn;

    public Hospital(int id, String name, String address, String phone, String inn) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.inn = inn;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getInn() { return inn; }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}
