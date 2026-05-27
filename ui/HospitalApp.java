package ui;

import db.Database;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HospitalApp extends JFrame {
    private Database db;

    private JTable patientTable, doctorTable, diagnosisTable, hospitalTable, departmentTable,
            positionTable, roomTable, medicineTable, appointmentTable, medicalRecordTable;
    private DefaultTableModel patientModel, doctorModel, diagnosisModel, hospitalModel, departmentModel,
            positionModel, roomModel, medicineModel, appointmentModel, medicalRecordModel;

    public HospitalApp() {
        db = new Database();
        initUI();
        loadAllData();
    }

    private void initUI() {
        setTitle("Больница - Система учета (ООП курсовая)");
        setSize(1300, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Пациенты", createPatientPanel());
        tabbedPane.addTab("Врачи", createDoctorPanel());
        tabbedPane.addTab("Диагнозы", createDiagnosisPanel());
        tabbedPane.addTab("Больницы", createHospitalPanel());
        tabbedPane.addTab("Отделения", createDepartmentPanel());
        tabbedPane.addTab("Должности", createPositionPanel());
        tabbedPane.addTab("Палаты", createRoomPanel());
        tabbedPane.addTab("Лекарства", createMedicinePanel());
        tabbedPane.addTab("Приёмы", createAppointmentPanel());
        tabbedPane.addTab("Мед. записи", createMedicalRecordPanel());

        add(tabbedPane);
    }

    private JPanel createButtonPanel(Runnable addAction, Runnable deleteAction, Runnable refreshAction) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addBtn = new JButton("Добавить");
        JButton deleteBtn = new JButton("Удалить");
        JButton refreshBtn = new JButton("Обновить");

        addBtn.addActionListener(e -> addAction.run());
        deleteBtn.addActionListener(e -> deleteAction.run());
        refreshBtn.addActionListener(e -> refreshAction.run());

        panel.add(addBtn);
        panel.add(deleteBtn);
        panel.add(refreshBtn);
        return panel;
    }

    // ==================== ПАЦИЕНТЫ ====================
    private JPanel createPatientPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "ФИО", "Дата рожд.", "Пол", "Адрес", "Телефон", "Статус"};
        patientModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        patientTable = new JTable(patientModel);
        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);

        JPanel btnPanel = createButtonPanel(this::addPatient, this::deletePatient, this::loadPatients);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadPatients() {
        patientModel.setRowCount(0);
        for (Patient p : db.getAllPatients()) {
            patientModel.addRow(new Object[]{
                    p.getId(), p.getFullName(), p.getBirthDate(), p.getGender(),
                    p.getAddress(), p.getPhone(), p.getStatus()
            });
        }
    }

    private void addPatient() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField birthField = new JTextField(20);
        JTextField genderField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField phoneField = new JTextField(20);

        JComboBox<Hospital> hospitalCombo = new JComboBox<>();
        JComboBox<Department> departmentCombo = new JComboBox<>();
        JComboBox<Doctor> doctorCombo = new JComboBox<>();
        JComboBox<Diagnosis> diagnosisCombo = new JComboBox<>();

        for (Hospital h : db.getAllHospitals()) hospitalCombo.addItem(h);
        for (Diagnosis d : db.getAllDiagnoses()) diagnosisCombo.addItem(d);

        hospitalCombo.addActionListener(e -> {
            Hospital h = (Hospital) hospitalCombo.getSelectedItem();
            if (h != null) {
                departmentCombo.removeAllItems();
                for (Department d : db.getDepartmentsByHospital(h.getId())) departmentCombo.addItem(d);
                doctorCombo.removeAllItems();
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
            }
        });

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("ФИО:"), gbc);
        gbc.gridx = 1; form.add(nameField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Дата рождения (ГГГГ-ММ-ДД):"), gbc);
        gbc.gridx = 1; form.add(birthField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Пол (М/Ж):"), gbc);
        gbc.gridx = 1; form.add(genderField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Адрес:"), gbc);
        gbc.gridx = 1; form.add(addressField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Телефон:"), gbc);
        gbc.gridx = 1; form.add(phoneField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Больница:"), gbc);
        gbc.gridx = 1; form.add(hospitalCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Отделение:"), gbc);
        gbc.gridx = 1; form.add(departmentCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Врач:"), gbc);
        gbc.gridx = 1; form.add(doctorCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Диагноз:"), gbc);
        gbc.gridx = 1; form.add(diagnosisCombo, gbc);

        JDialog dialog = new JDialog(this, "Добавление пациента", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Сохранить");
        JButton cancelBtn = new JButton("Отмена");
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

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

                Patient p = new Patient(0, nameField.getText(), birthField.getText(), genderField.getText(),
                        addressField.getText(), phoneField.getText(), h.getId(), dep.getId(),
                        doc != null ? doc.getId() : null, diag.getId(), "active");
                db.addPatient(p);
                loadPatients();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Пациент добавлен!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setSize(550, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deletePatient() {
        int row = patientTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите пациента!");
            return;
        }
        int id = (int) patientModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Удалить пациента?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            db.deletePatient(id);
            loadPatients();
            JOptionPane.showMessageDialog(this, "Пациент удалён!");
        }
    }

    // ==================== ВРАЧИ ====================
    private JPanel createDoctorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "ФИО", "Дата рожд.", "Телефон", "ИНН", "Должность", "Отделение", "Больница"};
        doctorModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        doctorTable = new JTable(doctorModel);
        panel.add(new JScrollPane(doctorTable), BorderLayout.CENTER);

        JPanel btnPanel = createButtonPanel(this::addDoctor, this::deleteDoctor, this::loadDoctors);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadDoctors() {
        doctorModel.setRowCount(0);
        for (Doctor d : db.getAllDoctors()) {
            String posName = "", depName = "", hosName = "";
            for (Position p : db.getAllPositions()) if (p.getId() == d.getPositionId()) posName = p.getTitle();
            for (Department dep : db.getAllDepartments()) if (dep.getId() == d.getDepartmentId()) depName = dep.getName();
            for (Hospital h : db.getAllHospitals()) if (h.getId() == d.getHospitalId()) hosName = h.getName();
            doctorModel.addRow(new Object[]{d.getId(), d.getFullName(), d.getBirthDate(), d.getPhone(), d.getInn(), posName, depName, hosName});
        }
    }

    private void addDoctor() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField birthField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField innField = new JTextField(20);

        JComboBox<Hospital> hospitalCombo = new JComboBox<>();
        JComboBox<Department> departmentCombo = new JComboBox<>();
        JComboBox<Position> positionCombo = new JComboBox<>();

        for (Hospital h : db.getAllHospitals()) hospitalCombo.addItem(h);
        for (Position p : db.getAllPositions()) positionCombo.addItem(p);

        hospitalCombo.addActionListener(e -> {
            Hospital h = (Hospital) hospitalCombo.getSelectedItem();
            if (h != null) {
                departmentCombo.removeAllItems();
                for (Department d : db.getDepartmentsByHospital(h.getId())) departmentCombo.addItem(d);
            }
        });

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("ФИО:"), gbc);
        gbc.gridx = 1; form.add(nameField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Дата рождения (ГГГГ-ММ-ДД):"), gbc);
        gbc.gridx = 1; form.add(birthField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Телефон:"), gbc);
        gbc.gridx = 1; form.add(phoneField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("ИНН (12 цифр):"), gbc);
        gbc.gridx = 1; form.add(innField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Больница:"), gbc);
        gbc.gridx = 1; form.add(hospitalCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Отделение:"), gbc);
        gbc.gridx = 1; form.add(departmentCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Должность:"), gbc);
        gbc.gridx = 1; form.add(positionCombo, gbc);

        JDialog dialog = new JDialog(this, "Добавление врача", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Сохранить");
        JButton cancelBtn = new JButton("Отмена");
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            try {
                Hospital h = (Hospital) hospitalCombo.getSelectedItem();
                Department dep = (Department) departmentCombo.getSelectedItem();
                Position pos = (Position) positionCombo.getSelectedItem();

                if (nameField.getText().isEmpty() || birthField.getText().isEmpty() || h == null || dep == null || pos == null) {
                    JOptionPane.showMessageDialog(dialog, "Заполните обязательные поля!");
                    return;
                }

                Doctor d = new Doctor(0, nameField.getText(), birthField.getText(), phoneField.getText(),
                        innField.getText(), pos.getId(), dep.getId(), h.getId());
                db.addDoctor(d);
                loadDoctors();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Врач добавлен!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteDoctor() {
        int row = doctorTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите врача!");
            return;
        }
        int id = (int) doctorModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Удалить врача?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            db.deleteDoctor(id);
            loadDoctors();
            JOptionPane.showMessageDialog(this, "Врач удалён!");
        }
    }

    // ==================== ДИАГНОЗЫ ====================
    private JPanel createDiagnosisPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Название", "Код МКБ", "Описание"};
        diagnosisModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        diagnosisTable = new JTable(diagnosisModel);
        panel.add(new JScrollPane(diagnosisTable), BorderLayout.CENTER);

        JPanel btnPanel = createButtonPanel(this::addDiagnosis, this::deleteDiagnosis, this::loadDiagnoses);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadDiagnoses() {
        diagnosisModel.setRowCount(0);
        for (Diagnosis d : db.getAllDiagnoses()) {
            diagnosisModel.addRow(new Object[]{d.getId(), d.getName(), d.getIcdCode(), d.getDescription()});
        }
    }

    private void addDiagnosis() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField codeField = new JTextField(20);
        JTextField descField = new JTextField(20);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Название:"), gbc);
        gbc.gridx = 1; form.add(nameField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Код МКБ:"), gbc);
        gbc.gridx = 1; form.add(codeField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Описание:"), gbc);
        gbc.gridx = 1; form.add(descField, gbc);

        JDialog dialog = new JDialog(this, "Добавление диагноза", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Сохранить");
        JButton cancelBtn = new JButton("Отмена");
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            if (nameField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Введите название!");
                return;
            }
            Diagnosis d = new Diagnosis(0, nameField.getText(), codeField.getText(), descField.getText());
            db.addDiagnosis(d);
            loadDiagnoses();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Диагноз добавлен!");
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteDiagnosis() {
        int row = diagnosisTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите диагноз!");
            return;
        }
        int id = (int) diagnosisModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Удалить диагноз?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            db.deleteDiagnosis(id);
            loadDiagnoses();
            JOptionPane.showMessageDialog(this, "Диагноз удалён!");
        }
    }

    // ==================== БОЛЬНИЦЫ ====================
    private JPanel createHospitalPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Название", "Адрес", "Телефон", "ИНН"};
        hospitalModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        hospitalTable = new JTable(hospitalModel);
        panel.add(new JScrollPane(hospitalTable), BorderLayout.CENTER);

        JPanel btnPanel = createButtonPanel(this::addHospital, this::deleteHospital, this::loadHospitals);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadHospitals() {
        hospitalModel.setRowCount(0);
        for (Hospital h : db.getAllHospitals()) {
            hospitalModel.addRow(new Object[]{h.getId(), h.getName(), h.getAddress(), h.getPhone(), h.getInn()});
        }
    }

    private void addHospital() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField innField = new JTextField(20);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Название:"), gbc);
        gbc.gridx = 1; form.add(nameField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Адрес:"), gbc);
        gbc.gridx = 1; form.add(addressField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Телефон:"), gbc);
        gbc.gridx = 1; form.add(phoneField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("ИНН:"), gbc);
        gbc.gridx = 1; form.add(innField, gbc);

        JDialog dialog = new JDialog(this, "Добавление больницы", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Сохранить");
        JButton cancelBtn = new JButton("Отмена");
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            if (nameField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Введите название!");
                return;
            }
            Hospital h = new Hospital(0, nameField.getText(), addressField.getText(), phoneField.getText(), innField.getText());
            db.addHospital(h);
            loadHospitals();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Больница добавлена!");
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteHospital() {
        int row = hospitalTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите больницу!");
            return;
        }
        int id = (int) hospitalModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Удалить больницу? (отделения и врачи тоже удалятся)", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            db.deleteHospital(id);
            loadHospitals();
            loadDepartments();
            loadDoctors();
            JOptionPane.showMessageDialog(this, "Больница удалена!");
        }
    }

    // ==================== ОТДЕЛЕНИЯ ====================
    private JPanel createDepartmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Название", "Зав. отделением", "Телефон", "Больница"};
        departmentModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        departmentTable = new JTable(departmentModel);
        panel.add(new JScrollPane(departmentTable), BorderLayout.CENTER);

        JPanel btnPanel = createButtonPanel(this::addDepartment, this::deleteDepartment, this::loadDepartments);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadDepartments() {
        departmentModel.setRowCount(0);
        for (Department d : db.getAllDepartments()) {
            String hospitalName = "";
            for (Hospital h : db.getAllHospitals()) if (h.getId() == d.getHospitalId()) hospitalName = h.getName();
            departmentModel.addRow(new Object[]{d.getId(), d.getName(), d.getHeadDoctor(), d.getPhoneExtension(), hospitalName});
        }
    }

    private void addDepartment() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField headField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JComboBox<Hospital> hospitalCombo = new JComboBox<>();

        for (Hospital h : db.getAllHospitals()) hospitalCombo.addItem(h);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Название:"), gbc);
        gbc.gridx = 1; form.add(nameField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Зав. отделением:"), gbc);
        gbc.gridx = 1; form.add(headField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Внутренний телефон:"), gbc);
        gbc.gridx = 1; form.add(phoneField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Больница:"), gbc);
        gbc.gridx = 1; form.add(hospitalCombo, gbc);

        JDialog dialog = new JDialog(this, "Добавление отделения", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Сохранить");
        JButton cancelBtn = new JButton("Отмена");
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            Hospital h = (Hospital) hospitalCombo.getSelectedItem();
            if (nameField.getText().isEmpty() || h == null) {
                JOptionPane.showMessageDialog(dialog, "Введите название и выберите больницу!");
                return;
            }
            Department d = new Department(0, nameField.getText(), headField.getText(), phoneField.getText(), h.getId());
            db.addDepartment(d);
            loadDepartments();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Отделение добавлено!");
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteDepartment() {
        int row = departmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите отделение!");
            return;
        }
        int id = (int) departmentModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Удалить отделение? (врачи тоже удалятся)", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            db.deleteDepartment(id);
            loadDepartments();
            loadDoctors();
            JOptionPane.showMessageDialog(this, "Отделение удалено!");
        }
    }

    // ==================== ДОЛЖНОСТИ ====================
    private JPanel createPositionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Название", "Оклад"};
        positionModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        positionTable = new JTable(positionModel);
        panel.add(new JScrollPane(positionTable), BorderLayout.CENTER);

        JPanel btnPanel = createButtonPanel(this::addPosition, this::deletePosition, this::loadPositions);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadPositions() {
        positionModel.setRowCount(0);
        for (Position p : db.getAllPositions()) {
            positionModel.addRow(new Object[]{p.getId(), p.getTitle(), p.getSalary()});
        }
    }

    private void addPosition() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField titleField = new JTextField(20);
        JTextField salaryField = new JTextField(20);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Название:"), gbc);
        gbc.gridx = 1; form.add(titleField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Оклад:"), gbc);
        gbc.gridx = 1; form.add(salaryField, gbc);

        JDialog dialog = new JDialog(this, "Добавление должности", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Сохранить");
        JButton cancelBtn = new JButton("Отмена");
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            if (titleField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Введите название!");
                return;
            }
            double salary = 0;
            try { salary = Double.parseDouble(salaryField.getText()); } catch (Exception ex) {}
            Position p = new Position(0, titleField.getText(), salary);
            db.addPosition(p);
            loadPositions();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Должность добавлена!");
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setSize(500, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deletePosition() {
        int row = positionTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите должность!");
            return;
        }
        int id = (int) positionModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Удалить должность?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            db.deletePosition(id);
            loadPositions();
            JOptionPane.showMessageDialog(this, "Должность удалена!");
        }
    }

    // ==================== ПАЛАТЫ ====================
    private JPanel createRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Номер", "Отделение", "Мест", "Свободна"};
        roomModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        roomTable = new JTable(roomModel);
        panel.add(new JScrollPane(roomTable), BorderLayout.CENTER);

        JPanel btnPanel = createButtonPanel(this::addRoom, this::deleteRoom, this::loadRooms);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadRooms() {
        roomModel.setRowCount(0);
        for (Room r : db.getAllRooms()) {
            String depName = "";
            for (Department d : db.getAllDepartments()) if (d.getId() == r.getDepartmentId()) depName = d.getName();
            roomModel.addRow(new Object[]{r.getId(), r.getRoomNumber(), depName, r.getBeds(), r.isFree() ? "Да" : "Нет"});
        }
    }

    private void addRoom() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField numberField = new JTextField(20);
        JComboBox<Department> departmentCombo = new JComboBox<>();
        JTextField bedsField = new JTextField(20);
        JComboBox<String> freeCombo = new JComboBox<>(new String[]{"Да", "Нет"});

        for (Department d : db.getAllDepartments()) departmentCombo.addItem(d);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Номер палаты:"), gbc);
        gbc.gridx = 1; form.add(numberField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Отделение:"), gbc);
        gbc.gridx = 1; form.add(departmentCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Количество мест:"), gbc);
        gbc.gridx = 1; form.add(bedsField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Свободна:"), gbc);
        gbc.gridx = 1; form.add(freeCombo, gbc);

        JDialog dialog = new JDialog(this, "Добавление палаты", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Сохранить");
        JButton cancelBtn = new JButton("Отмена");
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            Department dep = (Department) departmentCombo.getSelectedItem();
            if (numberField.getText().isEmpty() || dep == null) {
                JOptionPane.showMessageDialog(dialog, "Заполните номер и выберите отделение!");
                return;
            }
            int beds = 1;
            try { beds = Integer.parseInt(bedsField.getText()); } catch (Exception ex) {}
            Room r = new Room(0, Integer.parseInt(numberField.getText()), dep.getId(), beds, freeCombo.getSelectedItem().equals("Да"));
            db.addRoom(r);
            loadRooms();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Палата добавлена!");
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteRoom() {
        int row = roomTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите палату!");
            return;
        }
        int id = (int) roomModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Удалить палату?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            db.deleteRoom(id);
            loadRooms();
            JOptionPane.showMessageDialog(this, "Палата удалена!");
        }
    }

    // ==================== ЛЕКАРСТВА ====================
    private JPanel createMedicinePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Название", "Цена", "Количество"};
        medicineModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        medicineTable = new JTable(medicineModel);
        panel.add(new JScrollPane(medicineTable), BorderLayout.CENTER);

        JPanel btnPanel = createButtonPanel(this::addMedicine, this::deleteMedicine, this::loadMedicines);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadMedicines() {
        medicineModel.setRowCount(0);
        for (Medicine m : db.getAllMedicines()) {
            medicineModel.addRow(new Object[]{m.getId(), m.getName(), m.getPrice(), m.getQuantity()});
        }
    }

    private void addMedicine() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField quantityField = new JTextField(20);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Название:"), gbc);
        gbc.gridx = 1; form.add(nameField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Цена:"), gbc);
        gbc.gridx = 1; form.add(priceField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Количество:"), gbc);
        gbc.gridx = 1; form.add(quantityField, gbc);

        JDialog dialog = new JDialog(this, "Добавление лекарства", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Сохранить");
        JButton cancelBtn = new JButton("Отмена");
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            if (nameField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Введите название!");
                return;
            }
            double price = 0;
            int quantity = 0;
            try { price = Double.parseDouble(priceField.getText()); } catch (Exception ex) {}
            try { quantity = Integer.parseInt(quantityField.getText()); } catch (Exception ex) {}
            Medicine m = new Medicine(0, nameField.getText(), price, quantity);
            db.addMedicine(m);
            loadMedicines();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Лекарство добавлено!");
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteMedicine() {
        int row = medicineTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите лекарство!");
            return;
        }
        int id = (int) medicineModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Удалить лекарство?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            db.deleteMedicine(id);
            loadMedicines();
            JOptionPane.showMessageDialog(this, "Лекарство удалено!");
        }
    }

    // ==================== ПРИЁМЫ ====================
    private JPanel createAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Пациент", "Врач", "Дата", "Жалобы", "Статус"};
        appointmentModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        appointmentTable = new JTable(appointmentModel);
        panel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);

        JPanel btnPanel = createButtonPanel(this::addAppointment, this::deleteAppointment, this::loadAppointments);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadAppointments() {
        appointmentModel.setRowCount(0);
        for (Appointment a : db.getAllAppointments()) {
            String patientName = "", doctorName = "";
            for (Patient p : db.getAllPatients()) if (p.getId() == a.getPatientId()) patientName = p.getFullName();
            for (Doctor d : db.getAllDoctors()) if (d.getId() == a.getDoctorId()) doctorName = d.getFullName();
            appointmentModel.addRow(new Object[]{a.getId(), patientName, doctorName, a.getAppointmentDate(), a.getComplaints(), a.getStatus()});
        }
    }

    private void addAppointment() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<Patient> patientCombo = new JComboBox<>();
        JComboBox<Doctor> doctorCombo = new JComboBox<>();
        JTextField dateField = new JTextField(20);
        JTextField complaintsField = new JTextField(20);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"planned", "completed", "cancelled"});

        for (Patient p : db.getAllPatients()) patientCombo.addItem(p);
        for (Doctor d : db.getAllDoctors()) doctorCombo.addItem(d);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Пациент:"), gbc);
        gbc.gridx = 1; form.add(patientCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Врач:"), gbc);
        gbc.gridx = 1; form.add(doctorCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Дата (ГГГГ-ММ-ДД):"), gbc);
        gbc.gridx = 1; form.add(dateField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Жалобы:"), gbc);
        gbc.gridx = 1; form.add(complaintsField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Статус:"), gbc);
        gbc.gridx = 1; form.add(statusCombo, gbc);

        JDialog dialog = new JDialog(this, "Добавление приёма", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Сохранить");
        JButton cancelBtn = new JButton("Отмена");
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            Patient p = (Patient) patientCombo.getSelectedItem();
            Doctor d = (Doctor) doctorCombo.getSelectedItem();
            if (p == null || d == null || dateField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Заполните обязательные поля!");
                return;
            }
            Appointment a = new Appointment(0, p.getId(), d.getId(), dateField.getText(), complaintsField.getText(), (String) statusCombo.getSelectedItem());
            db.addAppointment(a);
            loadAppointments();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Приём добавлен!");
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteAppointment() {
        int row = appointmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите приём!");
            return;
        }
        int id = (int) appointmentModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Удалить приём?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            db.deleteAppointment(id);
            loadAppointments();
            JOptionPane.showMessageDialog(this, "Приём удалён!");
        }
    }

    // ==================== МЕДИЦИНСКИЕ ЗАПИСИ ====================
    private JPanel createMedicalRecordPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Пациент", "Диагноз", "Врач", "Дата", "Лечение", "Примечания"};
        medicalRecordModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        medicalRecordTable = new JTable(medicalRecordModel);
        panel.add(new JScrollPane(medicalRecordTable), BorderLayout.CENTER);

        JPanel btnPanel = createButtonPanel(this::addMedicalRecord, this::deleteMedicalRecord, this::loadMedicalRecords);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadMedicalRecords() {
        medicalRecordModel.setRowCount(0);
        for (MedicalRecord mr : db.getAllMedicalRecords()) {
            String patientName = "", diagnosisName = "", doctorName = "";
            for (Patient p : db.getAllPatients()) if (p.getId() == mr.getPatientId()) patientName = p.getFullName();
            for (Diagnosis d : db.getAllDiagnoses()) if (d.getId() == mr.getDiagnosisId()) diagnosisName = d.getName();
            for (Doctor d : db.getAllDoctors()) if (d.getId() == mr.getDoctorId()) doctorName = d.getFullName();
            medicalRecordModel.addRow(new Object[]{mr.getId(), patientName, diagnosisName, doctorName, mr.getRecordDate(), mr.getTreatment(), mr.getNotes()});
        }
    }

    private void addMedicalRecord() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<Patient> patientCombo = new JComboBox<>();
        JComboBox<Diagnosis> diagnosisCombo = new JComboBox<>();
        JComboBox<Doctor> doctorCombo = new JComboBox<>();
        JTextField dateField = new JTextField(20);
        JTextField treatmentField = new JTextField(20);
        JTextField notesField = new JTextField(20);

        for (Patient p : db.getAllPatients()) patientCombo.addItem(p);
        for (Diagnosis d : db.getAllDiagnoses()) diagnosisCombo.addItem(d);
        for (Doctor d : db.getAllDoctors()) doctorCombo.addItem(d);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Пациент:"), gbc);
        gbc.gridx = 1; form.add(patientCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Диагноз:"), gbc);
        gbc.gridx = 1; form.add(diagnosisCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Врач:"), gbc);
        gbc.gridx = 1; form.add(doctorCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Дата (ГГГГ-ММ-ДД):"), gbc);
        gbc.gridx = 1; form.add(dateField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Лечение:"), gbc);
        gbc.gridx = 1; form.add(treatmentField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Примечания:"), gbc);
        gbc.gridx = 1; form.add(notesField, gbc);

        JDialog dialog = new JDialog(this, "Добавление медицинской записи", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Сохранить");
        JButton cancelBtn = new JButton("Отмена");
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            Patient p = (Patient) patientCombo.getSelectedItem();
            Diagnosis d = (Diagnosis) diagnosisCombo.getSelectedItem();
            Doctor doc = (Doctor) doctorCombo.getSelectedItem();
            if (p == null || d == null || doc == null || dateField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Заполните обязательные поля!");
                return;
            }
            MedicalRecord mr = new MedicalRecord(0, p.getId(), d.getId(), doc.getId(), dateField.getText(), treatmentField.getText(), notesField.getText());
            db.addMedicalRecord(mr);
            loadMedicalRecords();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Запись добавлена!");
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setSize(550, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteMedicalRecord() {
        int row = medicalRecordTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите запись!");
            return;
        }
        int id = (int) medicalRecordModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Удалить запись?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            db.deleteMedicalRecord(id);
            loadMedicalRecords();
            JOptionPane.showMessageDialog(this, "Запись удалена!");
        }
    }

    private void loadAllData() {
        loadPatients();
        loadDoctors();
        loadDiagnoses();
        loadHospitals();
        loadDepartments();
        loadPositions();
        loadRooms();
        loadMedicines();
        loadAppointments();
        loadMedicalRecords();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HospitalApp().setVisible(true));
    }
}







