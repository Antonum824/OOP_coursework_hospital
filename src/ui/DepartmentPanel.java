package ui;

import db.Database;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DepartmentPanel {
    private Database db;
    private JPanel panel;
    private JTable table;
    private DefaultTableModel tableModel;

    public DepartmentPanel(Database db) {
        this.db = db;
        createPanel();
        loadData();
    }

    public JPanel getPanel() {
        return panel;
    }

    private void createPanel() {
        panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Название", "Зав. отделением", "Телефон", "Больница"};
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
                JOptionPane.showMessageDialog(this.panel, "Выберите отделение!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            showDialog(id);
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this.panel, "Выберите отделение!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this.panel, "Удалить отделение? (врачи тоже удалятся)", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                db.deleteDepartment(id);
                loadData();
                JOptionPane.showMessageDialog(this.panel, "Отделение удалено!");
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
        for (Department d : db.getAllDepartments()) {
            String hospitalName = "";
            for (Hospital h : db.getAllHospitals()) if (h.getId() == d.getHospitalId()) hospitalName = h.getName();
            tableModel.addRow(new Object[]{d.getId(), d.getName(), d.getHeadDoctor(), d.getPhoneExtension(), hospitalName});
        }
    }

    private void showDialog(Integer departmentId) {
        Department existing = null;
        if (departmentId != null) {
            for (Department d : db.getAllDepartments()) {
                if (d.getId() == departmentId) {
                    existing = d;
                    break;
                }
            }
            if (existing == null) return;
        }

        final Department finalExisting = existing;

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panel),
                departmentId == null ? "Добавление отделения" : "Редактирование отделения", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(panel);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(finalExisting != null ? finalExisting.getName() : "", 20);
        JTextField headField = new JTextField(finalExisting != null ? finalExisting.getHeadDoctor() : "", 20);
        JTextField phoneField = new JTextField(finalExisting != null ? finalExisting.getPhoneExtension() : "", 20);

        JComboBox<Hospital> hospitalCombo = new JComboBox<>();
        List<Hospital> hospitals = db.getAllHospitals();
        for (Hospital h : hospitals) hospitalCombo.addItem(h);

        if (finalExisting != null) {
            for (int i = 0; i < hospitals.size(); i++) {
                if (hospitals.get(i).getId() == finalExisting.getHospitalId()) {
                    hospitalCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Название:"), gbc);
        gbc.gridx = 1; dialog.add(nameField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Зав. отделением:"), gbc);
        gbc.gridx = 1; dialog.add(headField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Внутренний телефон:"), gbc);
        gbc.gridx = 1; dialog.add(phoneField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Больница:"), gbc);
        gbc.gridx = 1; dialog.add(hospitalCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JButton saveBtn = new JButton(departmentId == null ? "Сохранить" : "Обновить");
        saveBtn.addActionListener(e -> {
            Hospital h = (Hospital) hospitalCombo.getSelectedItem();
            if (nameField.getText().isEmpty() || h == null) {
                JOptionPane.showMessageDialog(dialog, "Введите название и выберите больницу!");
                return;
            }
            if (departmentId == null) {
                Department d = new Department(0, nameField.getText(), headField.getText(), phoneField.getText(), h.getId());
                db.addDepartment(d);
                JOptionPane.showMessageDialog(dialog, "Отделение добавлено!");
            } else {
                Department d = new Department(departmentId, nameField.getText(), headField.getText(), phoneField.getText(), h.getId());
                db.updateDepartment(d);
                JOptionPane.showMessageDialog(dialog, "Данные обновлены!");
            }
            dialog.dispose();
            loadData();
        });
        dialog.add(saveBtn, gbc);

        dialog.setVisible(true);
    }
}
