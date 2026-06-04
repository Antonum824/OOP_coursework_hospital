package ui;

import db.Database;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientPanel {
    private Database db;
    private JPanel panel;
    private JTable table;
    private DefaultTableModel tableModel;

    public PatientPanel(Database db) {
        this.db = db;
        createPanel();
        loadData();
    }

    public JPanel getPanel() {
        return panel;
    }

    private void createPanel() {
        panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "ФИО", "Дата рожд.", "Пол", "Адрес", "Телефон", "Статус"};
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
                JOptionPane.showMessageDialog(this.panel, "Выберите пациента!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            showDialog(id);
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this.panel, "Выберите пациента!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this.panel, "Удалить пациента?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                db.deletePatient(id);
                loadData();
                JOptionPane.showMessageDialog(this.panel, "Пациент удалён!");
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
        for (Patient p : db.getAllPatients()) {
            tableModel.addRow(new Object[]{
                    p.getId(), p.getFullName(), p.getBirthDate(), p.getGender(),
                    p.getAddress(), p.getPhone(), p.getStatus()
            });
        }
    }

    private void showDialog(Integer patientId) {
        // Загружаем существующего пациента (если редактирование)
        Patient existing = null;
        if (patientId != null) {
            for (Patient p : db.getAllPatients()) {
                if (p.getId() == patientId) {
                    existing = p;
                    break;
                }
            }
            if (existing == null) return;
        }

        // Сохраняем existing в final переменную для использования в лямбде
        final Patient finalExisting = existing;

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panel),
                patientId == null ? "Добавление пациента" : "Редактирование пациента", true);
        dialog.setSize(550, 650);
        dialog.setLocationRelativeTo(panel);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Поля с предзаполнением для редактирования
        JTextField nameField = new JTextField(finalExisting != null ? finalExisting.getFullName() : "", 20);
        JTextField birthField = new JTextField(finalExisting != null ? finalExisting.getBirthDate() : "", 20);
        JTextField genderField = new JTextField(finalExisting != null ? finalExisting.getGender() : "", 20);
        JTextField addressField = new JTextField(finalExisting != null ? finalExisting.getAddress() : "", 20);
        JTextField phoneField = new JTextField(finalExisting != null ? finalExisting.getPhone() : "", 20);

        JComboBox<Hospital> hospitalCombo = new JComboBox<>();
        JComboBox<Department> departmentCombo = new JComboBox<>();
        JComboBox<Doctor> doctorCombo = new JComboBox<>();
        JComboBox<Diagnosis> diagnosisCombo = new JComboBox<>();

        List<Hospital> hospitals = db.getAllHospitals();
        for (Hospital h : hospitals) hospitalCombo.addItem(h);

        List<Diagnosis> diagnoses = db.getAllDiagnoses();
        for (Diagnosis d : diagnoses) diagnosisCombo.addItem(d);

        // Устанавливаем текущие значения для выпадающих списков (при редактировании)
        if (finalExisting != null) {
            for (int i = 0; i < hospitals.size(); i++) {
                if (hospitals.get(i).getId() == finalExisting.getHospitalId()) {
                    hospitalCombo.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < diagnoses.size(); i++) {
                if (diagnoses.get(i).getId() == finalExisting.getDiagnosisId()) {
                    diagnosisCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        hospitalCombo.addActionListener(e -> {
            Hospital h = (Hospital) hospitalCombo.getSelectedItem();
            if (h != null) {
                departmentCombo.removeAllItems();
                for (Department d : db.getDepartmentsByHospital(h.getId())) {
                    departmentCombo.addItem(d);
                }
                doctorCombo.removeAllItems();
                // При редактировании восстанавливаем отделение
                if (finalExisting != null && h.getId() == finalExisting.getHospitalId()) {
                    for (int i = 0; i < departmentCombo.getItemCount(); i++) {
                        if (departmentCombo.getItemAt(i).getId() == finalExisting.getDepartmentId()) {
                            departmentCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        departmentCombo.addActionListener(e -> {
            Hospital h = (Hospital) hospitalCombo.getSelectedItem();
            Department d = (Department) departmentCombo.getSelectedItem();
            if (h != null && d != null) {
                doctorCombo.removeAllItems();
                for (Doctor doc : db.getAllDoctors()) {
                    if (doc.getHospitalId() == h.getId() && doc.getDepartmentId() == d.getId()) {
                        doctorCombo.addItem(doc);
                    }
                }
                // При редактировании восстанавливаем врача
                if (finalExisting != null && finalExisting.getDoctorId() != null) {
                    for (int i = 0; i < doctorCombo.getItemCount(); i++) {
                        if (doctorCombo.getItemAt(i).getId() == finalExisting.getDoctorId()) {
                            doctorCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        // Инициализация отделений и врачей при редактировании
        if (finalExisting != null) {
            // Инициализация отделений
            departmentCombo.removeAllItems();
            for (Department d : db.getDepartmentsByHospital(finalExisting.getHospitalId())) {
                departmentCombo.addItem(d);
            }
            for (int i = 0; i < departmentCombo.getItemCount(); i++) {
                if (departmentCombo.getItemAt(i).getId() == finalExisting.getDepartmentId()) {
                    departmentCombo.setSelectedIndex(i);
                    break;
                }
            }
            // Инициализация врачей
            doctorCombo.removeAllItems();
            for (Doctor doc : db.getAllDoctors()) {
                if (doc.getHospitalId() == finalExisting.getHospitalId() &&
                        doc.getDepartmentId() == finalExisting.getDepartmentId()) {
                    doctorCombo.addItem(doc);
                }
            }
            if (finalExisting.getDoctorId() != null) {
                for (int i = 0; i < doctorCombo.getItemCount(); i++) {
                    if (doctorCombo.getItemAt(i).getId() == finalExisting.getDoctorId()) {
                        doctorCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("ФИО:"), gbc);
        gbc.gridx = 1; dialog.add(nameField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Дата рождения (ГГГГ-ММ-ДД):"), gbc);
        gbc.gridx = 1; dialog.add(birthField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Пол (М/Ж):"), gbc);
        gbc.gridx = 1; dialog.add(genderField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Адрес:"), gbc);
        gbc.gridx = 1; dialog.add(addressField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Телефон:"), gbc);
        gbc.gridx = 1; dialog.add(phoneField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Больница:"), gbc);
        gbc.gridx = 1; dialog.add(hospitalCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Отделение:"), gbc);
        gbc.gridx = 1; dialog.add(departmentCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Врач:"), gbc);
        gbc.gridx = 1; dialog.add(doctorCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Диагноз:"), gbc);
        gbc.gridx = 1; dialog.add(diagnosisCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JButton saveBtn = new JButton(patientId == null ? "Сохранить" : "Обновить");
        saveBtn.addActionListener(e -> {
            try {
                Hospital h = (Hospital) hospitalCombo.getSelectedItem();
                Department dep = (Department) departmentCombo.getSelectedItem();
                Doctor doc = (Doctor) doctorCombo.getSelectedItem();
                Diagnosis diag = (Diagnosis) diagnosisCombo.getSelectedItem();

                if (nameField.getText().isEmpty() || birthField.getText().isEmpty() || h == null || dep == null || diag == null) {
                    JOptionPane.showMessageDialog(dialog, "Заполните обязательные поля!");
                    return;
                }

                if (patientId == null) {
                    // Добавление нового пациента
                    Patient p = new Patient(0, nameField.getText(), birthField.getText(), genderField.getText(),
                            addressField.getText(), phoneField.getText(), h.getId(), dep.getId(),
                            doc != null ? doc.getId() : null, diag.getId(), "active");
                    db.addPatient(p);
                    JOptionPane.showMessageDialog(dialog, "Пациент добавлен!");
                } else {
                    // Редактирование существующего пациента
                    Patient p = new Patient(patientId, nameField.getText(), birthField.getText(), genderField.getText(),
                            addressField.getText(), phoneField.getText(), h.getId(), dep.getId(),
                            doc != null ? doc.getId() : null, diag.getId(), finalExisting.getStatus());
                    db.updatePatient(p);
                    JOptionPane.showMessageDialog(dialog, "Данные обновлены!");
                }
                dialog.dispose();
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage());
            }
        });
        dialog.add(saveBtn, gbc);

        dialog.setVisible(true);
    }
}



