package model;

public abstract class Person {
    protected int id;
    protected String fullName;
    protected String birthDate;
    protected String phone;

    public Person(int id, String fullName, String birthDate, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.phone = phone;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getBirthDate() { return birthDate; }
    public String getPhone() { return phone; }

    @Override
    public String toString() {
        return id + " - " + fullName;
    }
}
