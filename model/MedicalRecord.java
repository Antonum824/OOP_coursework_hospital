package model;

public class MedicalRecord {
    private int id;
    private int patientId;
    private int diagnosisId;
    private int doctorId;
    private String recordDate;
    private String treatment;
    private String notes;

    public MedicalRecord(int id, int patientId, int diagnosisId, int doctorId,
                         String recordDate, String treatment, String notes) {
        this.id = id;
        this.patientId = patientId;
        this.diagnosisId = diagnosisId;
        this.doctorId = doctorId;
        this.recordDate = recordDate;
        this.treatment = treatment;
        this.notes = notes;
    }

    public int getId() { return id; }
    public int getPatientId() { return patientId; }
    public int getDiagnosisId() { return diagnosisId; }
    public int getDoctorId() { return doctorId; }
    public String getRecordDate() { return recordDate; }
    public String getTreatment() { return treatment; }
    public String getNotes() { return notes; }

    @Override
    public String toString() {
        return "Запись #" + id;
    }
}
