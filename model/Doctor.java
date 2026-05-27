package model;

public class Doctor {
    private int id;
    private String fullName;
    private String birthDate;
    private String phone;
    private String inn;
    private int positionId;
    private int departmentId;
    private int hospitalId;

    public Doctor(int id, String fullName, String birthDate, String phone,
                  String inn, int positionId, int departmentId, int hospitalId) {
        this.id = id;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.inn = inn;
        this.positionId = positionId;
        this.departmentId = departmentId;
        this.hospitalId = hospitalId;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getBirthDate() { return birthDate; }
    public String getPhone() { return phone; }
    public String getInn() { return inn; }
    public int getPositionId() { return positionId; }
    public int getDepartmentId() { return departmentId; }
    public int getHospitalId() { return hospitalId; }

    @Override
    public String toString() {
        return id + " - " + fullName;
    }
}
