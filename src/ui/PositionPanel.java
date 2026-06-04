package ui;

import db.Database;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PositionPanel {
    private Database db;
    private JPanel panel;
    private JTable table;
    private DefaultTableModel tableModel;

    public PositionPanel(Database db) {
        this.db = db;
        createPanel();
        loadData();
    }

    public JPanel getPanel() {
        return panel;
    }

    private void createPanel() {
        panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Название", "Оклад"};
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
                JOptionPane.showMessageDialog(this.panel, "Выберите должность!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            showDialog(id);
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this.panel, "Выберите должность!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this.panel, "Удалить должность?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                db.deletePosition(id);
                loadData();
                JOptionPane.showMessageDialog(this.panel, "Должность удалена!");
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
        for (Position p : db.getAllPositions()) {
            tableModel.addRow(new Object[]{p.getId(), p.getTitle(), p.getSalary()});
        }
    }

    private void showDialog(Integer positionId) {
        Position existing = null;
        if (positionId != null) {
            for (Position p : db.getAllPositions()) {
                if (p.getId() == positionId) {
                    existing = p;
                    break;
                }
            }
            if (existing == null) return;
        }

        final Position finalExisting = existing;

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panel),
                positionId == null ? "Добавление должности" : "Редактирование должности", true);
        dialog.setSize(500, 250);
        dialog.setLocationRelativeTo(panel);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField titleField = new JTextField(finalExisting != null ? finalExisting.getTitle() : "", 20);
        JTextField salaryField = new JTextField(finalExisting != null ? String.valueOf(finalExisting.getSalary()) : "", 20);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Название:"), gbc);
        gbc.gridx = 1; dialog.add(titleField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Оклад:"), gbc);
        gbc.gridx = 1; dialog.add(salaryField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JButton saveBtn = new JButton(positionId == null ? "Сохранить" : "Обновить");
        saveBtn.addActionListener(e -> {
            if (titleField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Введите название!");
                return;
            }
            double salary = 0;
            try { salary = Double.parseDouble(salaryField.getText()); } catch (Exception ex) {}
            if (positionId == null) {
                Position p = new Position(0, titleField.getText(), salary);
                db.addPosition(p);
                JOptionPane.showMessageDialog(dialog, "Должность добавлена!");
            } else {
                Position p = new Position(positionId, titleField.getText(), salary);
                db.updatePosition(p);
                JOptionPane.showMessageDialog(dialog, "Данные обновлены!");
            }
            dialog.dispose();
            loadData();
        });
        dialog.add(saveBtn, gbc);

        dialog.setVisible(true);
    }
}
