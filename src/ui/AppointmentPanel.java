package ui;

import db.Database;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AppointmentPanel {
    private Database db;
    private JPanel panel;
    private JTable table;
    private DefaultTableModel tableModel;

    public AppointmentPanel(Database db) {
        this.db = db;
        createPanel();
        loadData();
    }

    public JPanel getPanel() {
        return panel;
    }

    private void createPanel() {
        panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Пациент", "Врач", "Дата", "Жалобы", "Статус"};
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
                JOptionPane.showMessageDialog(this.panel, "Выберите приём!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            showDialog(id);
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this.panel, "Выберите приём!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this.panel, "Удалить приём?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                db.deleteAppointment(id);
                loadData();
                JOptionPane.showMessageDialog(this.panel, "Приём удалён!");
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
        for (Appointment a : db.getAllAppointments()) {
            String patientName = "", doctorName = "";
            for (Patient p : db.getAllPatients()) if (p.getId() == a.getPatientId()) patientName = p.getFullName();
            for (Doctor d : db.getAllDoctors()) if (d.getId() == a.getDoctorId()) doctorName = d.getFullName();
            tableModel.addRow(new Object[]{a.getId(), patientName, doctorName, a.getAppointmentDate(), a.getComplaints(), a.getStatus()});
        }
    }

    private void showDialog(Integer appointmentId) {
        Appointment existing = null;
        if (appointmentId != null) {
            for (Appointment a : db.getAllAppointments()) {
                if (a.getId() == appointmentId) {
                    existing = a;
                    break;
                }
            }
            if (existing == null) return;
        }

        final Appointment finalExisting = existing;

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panel),
                appointmentId == null ? "Добавление приёма" : "Редактирование приёма", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(panel);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<Patient> patientCombo = new JComboBox<>();
        JComboBox<Doctor> doctorCombo = new JComboBox<>();
        JTextField dateField = new JTextField(finalExisting != null ? finalExisting.getAppointmentDate() : "", 20);
        JTextField complaintsField = new JTextField(finalExisting != null ? finalExisting.getComplaints() : "", 20);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"planned", "completed", "cancelled"});

        List<Patient> patients = db.getAllPatients();
        for (Patient p : patients) patientCombo.addItem(p);

        List<Doctor> doctors = db.getAllDoctors();
        for (Doctor d : doctors) doctorCombo.addItem(d);

        if (finalExisting != null) {
            dateField.setText(finalExisting.getAppointmentDate());
            complaintsField.setText(finalExisting.getComplaints());
            statusCombo.setSelectedItem(finalExisting.getStatus());
            for (int i = 0; i < patients.size(); i++) {
                if (patients.get(i).getId() == finalExisting.getPatientId()) {
                    patientCombo.setSelectedIndex(i);
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
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Врач:"), gbc);
        gbc.gridx = 1; dialog.add(doctorCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Дата (ГГГГ-ММ-ДД):"), gbc);
        gbc.gridx = 1; dialog.add(dateField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Жалобы:"), gbc);
        gbc.gridx = 1; dialog.add(complaintsField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Статус:"), gbc);
        gbc.gridx = 1; dialog.add(statusCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JButton saveBtn = new JButton(appointmentId == null ? "Сохранить" : "Обновить");
        saveBtn.addActionListener(e -> {
            Patient p = (Patient) patientCombo.getSelectedItem();
            Doctor d = (Doctor) doctorCombo.getSelectedItem();
            if (p == null || d == null || dateField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Заполните обязательные поля!");
                return;
            }
            if (appointmentId == null) {
                Appointment a = new Appointment(0, p.getId(), d.getId(), dateField.getText(), complaintsField.getText(), (String) statusCombo.getSelectedItem());
                db.addAppointment(a);
                JOptionPane.showMessageDialog(dialog, "Приём добавлен!");
            } else {
                Appointment a = new Appointment(appointmentId, p.getId(), d.getId(), dateField.getText(), complaintsField.getText(), (String) statusCombo.getSelectedItem());
                db.updateAppointment(a);
                JOptionPane.showMessageDialog(dialog, "Данные обновлены!");
            }
            dialog.dispose();
            loadData();
        });
        dialog.add(saveBtn, gbc);

        dialog.setVisible(true);
    }
}
