package model;

public class Patient extends Person {
    private String gender;
    private String address;
    private int hospitalId;
    private int departmentId;
    private Integer attendingDoctorId;
    private int diagnosisId;
    private String status;

    public Patient(int id, String fullName, String birthDate, String gender,
                   String address, String phone, int hospitalId, int departmentId,
                   Integer attendingDoctorId, int diagnosisId, String status) {
        super(id, fullName, birthDate, phone);
        this.gender = gender;
        this.address = address;
        this.hospitalId = hospitalId;
        this.departmentId = departmentId;
        this.attendingDoctorId = attendingDoctorId;
        this.diagnosisId = diagnosisId;
        this.status = status;
    }

    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public int getHospitalId() { return hospitalId; }
    public int getDepartmentId() { return departmentId; }
    public Integer getAttendingDoctorId() { return attendingDoctorId; }
    public Integer getDoctorId() { return attendingDoctorId; }
    public int getDiagnosisId() { return diagnosisId; }
    public String getStatus() { return status; }
}
