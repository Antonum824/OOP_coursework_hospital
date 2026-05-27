package db;

import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private Connection connection;

    public Database() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/hospital",
                    "postgres",
                    "12345"
            );
            System.out.println("Подключение к БД успешно!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() { return connection; }

    // ========== PATIENTS ==========
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY patient_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                patients.add(new Patient(
                        rs.getInt("patient_id"),
                        rs.getString("full_name"),
                        rs.getString("birth_date"),
                        rs.getString("gender"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getInt("hospital_id"),
                        rs.getInt("department_id"),
                        rs.getInt("attending_doctor_id") == 0 ? null : rs.getInt("attending_doctor_id"),
                        rs.getInt("diagnosis_id"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return patients;
    }

    public void addPatient(Patient p) {
        String sql = "INSERT INTO patients (full_name, birth_date, gender, address, phone, " +
                "hospital_id, department_id, attending_doctor_id, diagnosis_id, hospitalization_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_DATE, 'active')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, p.getFullName());
            ps.setDate(2, java.sql.Date.valueOf(p.getBirthDate()));
            ps.setString(3, p.getGender());
            ps.setString(4, p.getAddress());
            ps.setString(5, p.getPhone());
            ps.setInt(6, p.getHospitalId());
            ps.setInt(7, p.getDepartmentId());
            if (p.getDoctorId() != null) ps.setInt(8, p.getDoctorId());
            else ps.setNull(8, Types.INTEGER);
            ps.setInt(9, p.getDiagnosisId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deletePatient(int id) {
        String sql = "DELETE FROM patients WHERE patient_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ========== DOCTORS ==========
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY doctor_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                doctors.add(new Doctor(
                        rs.getInt("doctor_id"),
                        rs.getString("full_name"),
                        rs.getString("birth_date"),
                        rs.getString("phone"),
                        rs.getString("inn"),
                        rs.getInt("position_id"),
                        rs.getInt("department_id"),
                        rs.getInt("hospital_id")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return doctors;
    }

    public void addDoctor(Doctor d) {
        String sql = "INSERT INTO doctors (full_name, birth_date, phone, inn, position_id, department_id, hospital_id, hire_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_DATE)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, d.getFullName());
            ps.setDate(2, java.sql.Date.valueOf(d.getBirthDate()));
            ps.setString(3, d.getPhone());
            ps.setString(4, d.getInn());
            ps.setInt(5, d.getPositionId());
            ps.setInt(6, d.getDepartmentId());
            ps.setInt(7, d.getHospitalId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteDoctor(int id) {
        String sql = "DELETE FROM doctors WHERE doctor_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ========== DIAGNOSES ==========
    public List<Diagnosis> getAllDiagnoses() {
        List<Diagnosis> diagnoses = new ArrayList<>();
        String sql = "SELECT * FROM diagnoses ORDER BY diagnosis_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                diagnoses.add(new Diagnosis(
                        rs.getInt("diagnosis_id"),
                        rs.getString("name"),
                        rs.getString("icd_code"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return diagnoses;
    }

    public void addDiagnosis(Diagnosis d) {
        String sql = "INSERT INTO diagnoses (name, icd_code, description) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getIcdCode());
            ps.setString(3, d.getDescription());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteDiagnosis(int id) {
        String sql = "DELETE FROM diagnoses WHERE diagnosis_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ========== HOSPITALS ==========
    public List<Hospital> getAllHospitals() {
        List<Hospital> hospitals = new ArrayList<>();
        String sql = "SELECT * FROM hospitals ORDER BY hospital_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                hospitals.add(new Hospital(
                        rs.getInt("hospital_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("inn")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return hospitals;
    }

    public void addHospital(Hospital h) {
        String sql = "INSERT INTO hospitals (name, address, phone, inn) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, h.getName());
            ps.setString(2, h.getAddress());
            ps.setString(3, h.getPhone());
            ps.setString(4, h.getInn());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteHospital(int id) {
        String sql = "DELETE FROM hospitals WHERE hospital_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ========== DEPARTMENTS ==========
    public List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM departments ORDER BY department_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                departments.add(new Department(
                        rs.getInt("department_id"),
                        rs.getString("name"),
                        rs.getString("head_doctor"),
                        rs.getString("phone_extension"),
                        rs.getInt("hospital_id")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return departments;
    }

    public List<Department> getDepartmentsByHospital(int hospitalId) {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM departments WHERE hospital_id = ? ORDER BY department_id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                departments.add(new Department(
                        rs.getInt("department_id"),
                        rs.getString("name"),
                        rs.getString("head_doctor"),
                        rs.getString("phone_extension"),
                        rs.getInt("hospital_id")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return departments;
    }

    public void addDepartment(Department d) {
        String sql = "INSERT INTO departments (name, head_doctor, phone_extension, hospital_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getHeadDoctor());
            ps.setString(3, d.getPhoneExtension());
            ps.setInt(4, d.getHospitalId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteDepartment(int id) {
        String sql = "DELETE FROM departments WHERE department_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ========== POSITIONS ==========
    public List<Position> getAllPositions() {
        List<Position> positions = new ArrayList<>();
        String sql = "SELECT * FROM positions ORDER BY position_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                positions.add(new Position(
                        rs.getInt("position_id"),
                        rs.getString("title"),
                        rs.getDouble("salary")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return positions;
    }

    public void addPosition(Position p) {
        String sql = "INSERT INTO positions (title, salary) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, p.getTitle());
            ps.setDouble(2, p.getSalary());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deletePosition(int id) {
        String sql = "DELETE FROM positions WHERE position_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ========== ROOMS ==========
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rooms.add(new Room(
                        rs.getInt("room_id"),
                        rs.getInt("room_number"),
                        rs.getInt("department_id"),
                        rs.getInt("beds"),
                        rs.getBoolean("is_free")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rooms;
    }

    public void addRoom(Room r) {
        String sql = "INSERT INTO rooms (room_number, department_id, beds, is_free) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, r.getRoomNumber());
            ps.setInt(2, r.getDepartmentId());
            ps.setInt(3, r.getBeds());
            ps.setBoolean(4, r.isFree());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteRoom(int id) {
        String sql = "DELETE FROM rooms WHERE room_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ========== MEDICINES ==========
    public List<Medicine> getAllMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines ORDER BY medicine_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                medicines.add(new Medicine(
                        rs.getInt("medicine_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return medicines;
    }

    public void addMedicine(Medicine m) {
        String sql = "INSERT INTO medicines (name, price, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setDouble(2, m.getPrice());
            ps.setInt(3, m.getQuantity());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteMedicine(int id) {
        String sql = "DELETE FROM medicines WHERE medicine_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ========== APPOINTMENTS ==========
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                appointments.add(new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getString("appointment_date"),
                        rs.getString("complaints"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return appointments;
    }

    public void addAppointment(Appointment a) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, complaints, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, a.getPatientId());
            ps.setInt(2, a.getDoctorId());
            ps.setString(3, a.getAppointmentDate());
            ps.setString(4, a.getComplaints());
            ps.setString(5, a.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteAppointment(int id) {
        String sql = "DELETE FROM appointments WHERE appointment_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ========== MEDICAL RECORDS ==========
    public List<MedicalRecord> getAllMedicalRecords() {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM medical_records ORDER BY record_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                records.add(new MedicalRecord(
                        rs.getInt("record_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("diagnosis_id"),
                        rs.getInt("doctor_id"),
                        rs.getString("record_date"),
                        rs.getString("treatment"),
                        rs.getString("notes")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return records;
    }

    public void addMedicalRecord(MedicalRecord mr) {
        String sql = "INSERT INTO medical_records (patient_id, diagnosis_id, doctor_id, record_date, treatment, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, mr.getPatientId());
            ps.setInt(2, mr.getDiagnosisId());
            ps.setInt(3, mr.getDoctorId());
            ps.setString(4, mr.getRecordDate());
            ps.setString(5, mr.getTreatment());
            ps.setString(6, mr.getNotes());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteMedicalRecord(int id) {
        String sql = "DELETE FROM medical_records WHERE record_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void close() {
        try { if (connection != null) connection.close(); }
        catch (SQLException e) { e.printStackTrace(); }
    }
}






