package model;

public class Appointment {
    private int id;
    private int patientId;
    private int doctorId;
    private String appointmentDate;
    private String complaints;
    private String status;

    public Appointment(int id, int patientId, int doctorId, String appointmentDate, String complaints, String status) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.complaints = complaints;
        this.status = status;
    }

    public int getId() { return id; }
    public int getPatientId() { return patientId; }
    public int getDoctorId() { return doctorId; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getComplaints() { return complaints; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return "Приём #" + id;
    }
}
