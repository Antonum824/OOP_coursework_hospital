package model;

public class Doctor extends Person {
    private String inn;
    private int positionId;
    private int departmentId;
    private int hospitalId;

    public Doctor(int id, String fullName, String birthDate, String phone,
                  String inn, int positionId, int departmentId, int hospitalId) {
        super(id, fullName, birthDate, phone);
        this.inn = inn;
        this.positionId = positionId;
        this.departmentId = departmentId;
        this.hospitalId = hospitalId;
    }

    public String getInn() { return inn; }
    public int getPositionId() { return positionId; }
    public int getDepartmentId() { return departmentId; }
    public int getHospitalId() { return hospitalId; }
}
