package ui;

import db.Database;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MedicalRecordPanel {
    private Database db;
    private JPanel panel;
    private JTable table;
    private DefaultTableModel tableModel;

    public MedicalRecordPanel(Database db) {
        this.db = db;
        createPanel();
        loadData();
    }

    public JPanel getPanel() {
        return panel;
    }

    private void createPanel() {
        panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Пациент", "Диагноз", "Врач", "Дата", "Лечение", "Примечания"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = createButtonPanel();
        panel.add(btnPanel, BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addBtn = new JButton("Добавить");
        JButton editBtn = new JButton("Изменить");
        JButton deleteBtn = new JButton("Удалить");
        JButton refreshBtn = new JButton("Обновить");

        addBtn.addActionListener(e -> showDialog(null));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this.panel, "Выберите запись!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            showDialog(id);
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this.panel, "Выберите запись!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this.panel, "Удалить запись?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                db.deleteMedicalRecord(id);
                loadData();
                JOptionPane.showMessageDialog(this.panel, "Запись удалена!");
            }
        });
        refreshBtn.addActionListener(e -> loadData());

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(refreshBtn);
        return panel;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (MedicalRecord mr : db.getAllMedicalRecords()) {
            String patientName = "", diagnosisName = "", doctorName = "";
            for (Patient p : db.getAllPatients()) if (p.getId() == mr.getPatientId()) patientName = p.getFullName();
            for (Diagnosis d : db.getAllDiagnoses()) if (d.getId() == mr.getDiagnosisId()) diagnosisName = d.getName();
            for (Doctor d : db.getAllDoctors()) if (d.getId() == mr.getDoctorId()) doctorName = d.getFullName();
            tableModel.addRow(new Object[]{mr.getId(), patientName, diagnosisName, doctorName, mr.getRecordDate(), mr.getTreatment(), mr.getNotes()});
        }
    }

    private void showDialog(Integer recordId) {
        MedicalRecord existing = null;
        if (recordId != null) {
            for (MedicalRecord mr : db.getAllMedicalRecords()) {
                if (mr.getId() == recordId) {
                    existing = mr;
                    break;
                }
            }
            if (existing == null) return;
        }

        final MedicalRecord finalExisting = existing;

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panel),
                recordId == null ? "Добавление медицинской записи" : "Редактирование медицинской записи", true);
        dialog.setSize(550, 500);
        dialog.setLocationRelativeTo(panel);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<Patient> patientCombo = new JComboBox<>();
        JComboBox<Diagnosis> diagnosisCombo = new JComboBox<>();
        JComboBox<Doctor> doctorCombo = new JComboBox<>();
        JTextField dateField = new JTextField(finalExisting != null ? finalExisting.getRecordDate() : "", 20);
        JTextField treatmentField = new JTextField(finalExisting != null ? finalExisting.getTreatment() : "", 20);
        JTextField notesField = new JTextField(finalExisting != null ? finalExisting.getNotes() : "", 20);

        List<Patient> patients = db.getAllPatients();
        for (Patient p : patients) patientCombo.addItem(p);

        List<Diagnosis> diagnoses = db.getAllDiagnoses();
        for (Diagnosis d : diagnoses) diagnosisCombo.addItem(d);

        List<Doctor> doctors = db.getAllDoctors();
        for (Doctor d : doctors) doctorCombo.addItem(d);

        if (finalExisting != null) {
            dateField.setText(finalExisting.getRecordDate());
            treatmentField.setText(finalExisting.getTreatment());
            notesField.setText(finalExisting.getNotes());
            for (int i = 0; i < patients.size(); i++) {
                if (patients.get(i).getId() == finalExisting.getPatientId()) {
                    patientCombo.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < diagnoses.size(); i++) {
                if (diagnoses.get(i).getId() == finalExisting.getDiagnosisId()) {
                    diagnosisCombo.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < doctors.size(); i++) {
                if (doctors.get(i).getId() == finalExisting.getDoctorId()) {
                    doctorCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Пациент:"), gbc);
        gbc.gridx = 1; dialog.add(patientCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Диагноз:"), gbc);
        gbc.gridx = 1; dialog.add(diagnosisCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Врач:"), gbc);
        gbc.gridx = 1; dialog.add(doctorCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Дата (ГГГГ-ММ-ДД):"), gbc);
        gbc.gridx = 1; dialog.add(dateField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Лечение:"), gbc);
        gbc.gridx = 1; dialog.add(treatmentField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Примечания:"), gbc);
        gbc.gridx = 1; dialog.add(notesField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JButton saveBtn = new JButton(recordId == null ? "Сохранить" : "Обновить");
        saveBtn.addActionListener(e -> {
            Patient p = (Patient) patientCombo.getSelectedItem();
            Diagnosis d = (Diagnosis) diagnosisCombo.getSelectedItem();
            Doctor doc = (Doctor) doctorCombo.getSelectedItem();
            if (p == null || d == null || doc == null || dateField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Заполните обязательные поля!");
                return;
            }
            if (recordId == null) {
                MedicalRecord mr = new MedicalRecord(0, p.getId(), d.getId(), doc.getId(), dateField.getText(), treatmentField.getText(), notesField.getText());
                db.addMedicalRecord(mr);
                JOptionPane.showMessageDialog(dialog, "Запись добавлена!");
            } else {
                MedicalRecord mr = new MedicalRecord(recordId, p.getId(), d.getId(), doc.getId(), dateField.getText(), treatmentField.getText(), notesField.getText());
                db.updateMedicalRecord(mr);
                JOptionPane.showMessageDialog(dialog, "Данные обновлены!");
            }
            dialog.dispose();
            loadData();
        });
        dialog.add(saveBtn, gbc);

        dialog.setVisible(true);
    }
}
