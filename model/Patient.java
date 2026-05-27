package model;

public class Patient {
    private int id;
    private String fullName;
    private String birthDate;
    private String gender;
    private String address;
    private String phone;
    private int hospitalId;
    private int departmentId;
    private Integer doctorId;
    private int diagnosisId;
    private String status;
    public Patient(int id, String fullName, String birthDate, String gender,
                   String address, String phone, int hospitalId, int departmentId,
                   Integer doctorId, int diagnosisId, String status) {
        this.id = id;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
        this.hospitalId = hospitalId;
        this.departmentId = departmentId;
        this.doctorId = doctorId;
        this.diagnosisId = diagnosisId;
        this.status = status;
    }
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getBirthDate() { return birthDate; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public int getHospitalId() { return hospitalId; }
    public int getDepartmentId() { return departmentId; }
    public Integer getDoctorId() { return doctorId; }
    public int getDiagnosisId() { return diagnosisId; }
    public String getStatus() { return status; }
    @Override
    public String toString() {
        return id + " - " + fullName;
    }
}
