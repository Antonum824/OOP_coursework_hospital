package ui;

import db.Database;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class HospitalPanel {
    private Database db;
    private JPanel panel;
    private JTable table;
    private DefaultTableModel tableModel;

    public HospitalPanel(Database db) {
        this.db = db;
        createPanel();
        loadData();
    }

    public JPanel getPanel() {
        return panel;
    }

    private void createPanel() {
        panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Название", "Адрес", "Телефон", "ИНН"};
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
                JOptionPane.showMessageDialog(this.panel, "Выберите больницу!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            showDialog(id);
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this.panel, "Выберите больницу!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this.panel, "Удалить больницу? (отделения и врачи тоже удалятся)", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                db.deleteHospital(id);
                loadData();
                JOptionPane.showMessageDialog(this.panel, "Больница удалена!");
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
        for (Hospital h : db.getAllHospitals()) {
            tableModel.addRow(new Object[]{h.getId(), h.getName(), h.getAddress(), h.getPhone(), h.getInn()});
        }
    }

    private void showDialog(Integer hospitalId) {
        Hospital existing = null;
        if (hospitalId != null) {
            for (Hospital h : db.getAllHospitals()) {
                if (h.getId() == hospitalId) {
                    existing = h;
                    break;
                }
            }
            if (existing == null) return;
        }

        final Hospital finalExisting = existing;

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panel),
                hospitalId == null ? "Добавление больницы" : "Редактирование больницы", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(panel);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(finalExisting != null ? finalExisting.getName() : "", 20);
        JTextField addressField = new JTextField(finalExisting != null ? finalExisting.getAddress() : "", 20);
        JTextField phoneField = new JTextField(finalExisting != null ? finalExisting.getPhone() : "", 20);
        JTextField innField = new JTextField(finalExisting != null ? finalExisting.getInn() : "", 20);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Название:"), gbc);
        gbc.gridx = 1; dialog.add(nameField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Адрес:"), gbc);
        gbc.gridx = 1; dialog.add(addressField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Телефон:"), gbc);
        gbc.gridx = 1; dialog.add(phoneField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("ИНН:"), gbc);
        gbc.gridx = 1; dialog.add(innField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JButton saveBtn = new JButton(hospitalId == null ? "Сохранить" : "Обновить");
        saveBtn.addActionListener(e -> {
            if (nameField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Введите название!");
                return;
            }
            if (hospitalId == null) {
                Hospital h = new Hospital(0, nameField.getText(), addressField.getText(), phoneField.getText(), innField.getText());
                db.addHospital(h);
                JOptionPane.showMessageDialog(dialog, "Больница добавлена!");
            } else {
                Hospital h = new Hospital(hospitalId, nameField.getText(), addressField.getText(), phoneField.getText(), innField.getText());
                db.updateHospital(h);
                JOptionPane.showMessageDialog(dialog, "Данные обновлены!");
            }
            dialog.dispose();
            loadData();
        });
        dialog.add(saveBtn, gbc);

        dialog.setVisible(true);
    }
}
