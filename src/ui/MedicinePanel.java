package ui;

import db.Database;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MedicinePanel {
    private Database db;
    private JPanel panel;
    private JTable table;
    private DefaultTableModel tableModel;

    public MedicinePanel(Database db) {
        this.db = db;
        createPanel();
        loadData();
    }

    public JPanel getPanel() {
        return panel;
    }

    private void createPanel() {
        panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Название", "Цена", "Количество"};
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
                JOptionPane.showMessageDialog(this.panel, "Выберите лекарство!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            showDialog(id);
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this.panel, "Выберите лекарство!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this.panel, "Удалить лекарство?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                db.deleteMedicine(id);
                loadData();
                JOptionPane.showMessageDialog(this.panel, "Лекарство удалено!");
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
        for (Medicine m : db.getAllMedicines()) {
            tableModel.addRow(new Object[]{m.getId(), m.getName(), m.getPrice(), m.getQuantity()});
        }
    }

    private void showDialog(Integer medicineId) {
        Medicine existing = null;
        if (medicineId != null) {
            for (Medicine m : db.getAllMedicines()) {
                if (m.getId() == medicineId) {
                    existing = m;
                    break;
                }
            }
            if (existing == null) return;
        }

        final Medicine finalExisting = existing;

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panel),
                medicineId == null ? "Добавление лекарства" : "Редактирование лекарства", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(panel);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(finalExisting != null ? finalExisting.getName() : "", 20);
        JTextField priceField = new JTextField(finalExisting != null ? String.valueOf(finalExisting.getPrice()) : "", 20);
        JTextField quantityField = new JTextField(finalExisting != null ? String.valueOf(finalExisting.getQuantity()) : "", 20);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Название:"), gbc);
        gbc.gridx = 1; dialog.add(nameField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Цена:"), gbc);
        gbc.gridx = 1; dialog.add(priceField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Количество:"), gbc);
        gbc.gridx = 1; dialog.add(quantityField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JButton saveBtn = new JButton(medicineId == null ? "Сохранить" : "Обновить");
        saveBtn.addActionListener(e -> {
            if (nameField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Введите название!");
                return;
            }
            double price = 0;
            int quantity = 0;
            try { price = Double.parseDouble(priceField.getText()); } catch (Exception ex) {}
            try { quantity = Integer.parseInt(quantityField.getText()); } catch (Exception ex) {}
            if (medicineId == null) {
                Medicine m = new Medicine(0, nameField.getText(), price, quantity);
                db.addMedicine(m);
                JOptionPane.showMessageDialog(dialog, "Лекарство добавлено!");
            } else {
                Medicine m = new Medicine(medicineId, nameField.getText(), price, quantity);
                db.updateMedicine(m);
                JOptionPane.showMessageDialog(dialog, "Данные обновлены!");
            }
            dialog.dispose();
            loadData();
        });
        dialog.add(saveBtn, gbc);

        dialog.setVisible(true);
    }
}
