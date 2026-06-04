package ui;

import db.Database;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DoctorPanel {
    private Database db;
    private JPanel panel;
    private JTable table;
    private DefaultTableModel tableModel;

    public DoctorPanel(Database db) {
        this.db = db;
        createPanel();
        loadData();
    }

    public JPanel getPanel() {
        return panel;
    }

    private void createPanel() {
        panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "ФИО", "Дата рожд.", "Телефон", "ИНН", "Должность", "Отделение", "Больница"};
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
                JOptionPane.showMessageDialog(this.panel, "Выберите врача!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            showDialog(id);
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this.panel, "Выберите врача!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this.panel, "Удалить врача?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                db.deleteDoctor(id);
                loadData();
                JOptionPane.showMessageDialog(this.panel, "Врач удалён!");
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
        for (Doctor d : db.getAllDoctors()) {
            String posName = "", depName = "", hosName = "";
            for (Position p : db.getAllPositions()) if (p.getId() == d.getPositionId()) posName = p.getTitle();
            for (Department dep : db.getAllDepartments()) if (dep.getId() == d.getDepartmentId()) depName = dep.getName();
            for (Hospital h : db.getAllHospitals()) if (h.getId() == d.getHospitalId()) hosName = h.getName();
            tableModel.addRow(new Object[]{d.getId(), d.getFullName(), d.getBirthDate(), d.getPhone(), d.getInn(), posName, depName, hosName});
        }
    }

    private void showDialog(Integer doctorId) {
        Doctor existing = null;
        if (doctorId != null) {
            for (Doctor d : db.getAllDoctors()) {
                if (d.getId() == doctorId) {
                    existing = d;
                    break;
                }
            }
            if (existing == null) return;
        }

        final Doctor finalExisting = existing;

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panel),
                doctorId == null ? "Добавление врача" : "Редактирование врача", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(panel);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(finalExisting != null ? finalExisting.getFullName() : "", 20);
        JTextField birthField = new JTextField(finalExisting != null ? finalExisting.getBirthDate() : "", 20);
        JTextField phoneField = new JTextField(finalExisting != null ? finalExisting.getPhone() : "", 20);
        JTextField innField = new JTextField(finalExisting != null ? finalExisting.getInn() : "", 20);

        JComboBox<Hospital> hospitalCombo = new JComboBox<>();
        JComboBox<Department> departmentCombo = new JComboBox<>();
        JComboBox<Position> positionCombo = new JComboBox<>();

        List<Hospital> hospitals = db.getAllHospitals();
        for (Hospital h : hospitals) hospitalCombo.addItem(h);

        List<Position> positions = db.getAllPositions();
        for (Position p : positions) positionCombo.addItem(p);

        if (finalExisting != null) {
            for (int i = 0; i < hospitals.size(); i++) {
                if (hospitals.get(i).getId() == finalExisting.getHospitalId()) {
                    hospitalCombo.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < positions.size(); i++) {
                if (positions.get(i).getId() == finalExisting.getPositionId()) {
                    positionCombo.setSelectedIndex(i);
                    break;
                }
            }
            // Загружаем отделения для текущей больницы
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
        }

        hospitalCombo.addActionListener(e -> {
            Hospital h = (Hospital) hospitalCombo.getSelectedItem();
            if (h != null) {
                departmentCombo.removeAllItems();
                for (Department d : db.getDepartmentsByHospital(h.getId())) {
                    departmentCombo.addItem(d);
                }
            }
        });

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("ФИО:"), gbc);
        gbc.gridx = 1; dialog.add(nameField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Дата рождения (ГГГГ-ММ-ДД):"), gbc);
        gbc.gridx = 1; dialog.add(birthField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Телефон:"), gbc);
        gbc.gridx = 1; dialog.add(phoneField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("ИНН (12 цифр):"), gbc);
        gbc.gridx = 1; dialog.add(innField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Больница:"), gbc);
        gbc.gridx = 1; dialog.add(hospitalCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Отделение:"), gbc);
        gbc.gridx = 1; dialog.add(departmentCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Должность:"), gbc);
        gbc.gridx = 1; dialog.add(positionCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JButton saveBtn = new JButton(doctorId == null ? "Сохранить" : "Обновить");
        saveBtn.addActionListener(e -> {
            try {
                Hospital h = (Hospital) hospitalCombo.getSelectedItem();
                Department dep = (Department) departmentCombo.getSelectedItem();
                Position pos = (Position) positionCombo.getSelectedItem();

                if (nameField.getText().isEmpty() || birthField.getText().isEmpty() || h == null || dep == null || pos == null) {
                    JOptionPane.showMessageDialog(dialog, "Заполните обязательные поля!");
                    return;
                }

                if (doctorId == null) {
                    Doctor d = new Doctor(0, nameField.getText(), birthField.getText(), phoneField.getText(),
                            innField.getText(), pos.getId(), dep.getId(), h.getId());
                    db.addDoctor(d);
                    JOptionPane.showMessageDialog(dialog, "Врач добавлен!");
                } else {
                    Doctor d = new Doctor(doctorId, nameField.getText(), birthField.getText(), phoneField.getText(),
                            innField.getText(), pos.getId(), dep.getId(), h.getId());
                    db.updateDoctor(d);
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
