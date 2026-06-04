package ui;

import db.Database;
import javax.swing.*;

public class HospitalApp extends JFrame {
    private Database db;

    public HospitalApp() {
        db = new Database();
        initUI();
    }

    private void initUI() {
        setTitle("Больница - Система учета (ООП курсовая)");
        setSize(1300, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Пациенты", new PatientPanel(db).getPanel());
        tabbedPane.addTab("Врачи", new DoctorPanel(db).getPanel());
        tabbedPane.addTab("Диагнозы", new DiagnosisPanel(db).getPanel());
        tabbedPane.addTab("Больницы", new HospitalPanel(db).getPanel());
        tabbedPane.addTab("Отделения", new DepartmentPanel(db).getPanel());
        tabbedPane.addTab("Должности", new PositionPanel(db).getPanel());
        tabbedPane.addTab("Палаты", new RoomPanel(db).getPanel());
        tabbedPane.addTab("Лекарства", new MedicinePanel(db).getPanel());
        tabbedPane.addTab("Приёмы", new AppointmentPanel(db).getPanel());
        tabbedPane.addTab("Мед. записи", new MedicalRecordPanel(db).getPanel());

        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HospitalApp().setVisible(true));
    }
}








