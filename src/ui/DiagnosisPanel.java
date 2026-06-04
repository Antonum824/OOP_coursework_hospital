package ui;

import db.Database;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DiagnosisPanel {
    private Database db;
    private JPanel panel;
    private JTable table;
    private DefaultTableModel tableModel;

    public DiagnosisPanel(Database db) {
        this.db = db;
        createPanel();
        loadData();
    }

    public JPanel getPanel() {
        return panel;
    }

    private void createPanel() {
        panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Название", "Код МКБ", "Описание"};
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
                JOptionPane.showMessageDialog(this.panel, "Выберите диагноз!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            showDialog(id);
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this.panel, "Выберите диагноз!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this.panel, "Удалить диагноз?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                db.deleteDiagnosis(id);
                loadData();
                JOptionPane.showMessageDialog(this.panel, "Диагноз удалён!");
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
        for (Diagnosis d : db.getAllDiagnoses()) {
            tableModel.addRow(new Object[]{d.getId(), d.getName(), d.getIcdCode(), d.getDescription()});
        }
    }

    private void showDialog(Integer diagnosisId) {
        Diagnosis existing = null;
        if (diagnosisId != null) {
            for (Diagnosis d : db.getAllDiagnoses()) {
                if (d.getId() == diagnosisId) {
                    existing = d;
                    break;
                }
            }
            if (existing == null) return;
        }

        final Diagnosis finalExisting = existing;

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panel),
                diagnosisId == null ? "Добавление диагноза" : "Редактирование диагноза", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(panel);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(finalExisting != null ? finalExisting.getName() : "", 20);
        JTextField codeField = new JTextField(finalExisting != null ? finalExisting.getIcdCode() : "", 20);
        JTextField descField = new JTextField(finalExisting != null ? finalExisting.getDescription() : "", 20);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Название:"), gbc);
        gbc.gridx = 1; dialog.add(nameField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Код МКБ:"), gbc);
        gbc.gridx = 1; dialog.add(codeField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Описание:"), gbc);
        gbc.gridx = 1; dialog.add(descField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JButton saveBtn = new JButton(diagnosisId == null ? "Сохранить" : "Обновить");
        saveBtn.addActionListener(e -> {
            if (nameField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Введите название!");
                return;
            }
            if (diagnosisId == null) {
                Diagnosis d = new Diagnosis(0, nameField.getText(), codeField.getText(), descField.getText());
                db.addDiagnosis(d);
                JOptionPane.showMessageDialog(dialog, "Диагноз добавлен!");
            } else {
                Diagnosis d = new Diagnosis(diagnosisId, nameField.getText(), codeField.getText(), descField.getText());
                db.updateDiagnosis(d);
                JOptionPane.showMessageDialog(dialog, "Данные обновлены!");
            }
            dialog.dispose();
            loadData();
        });
        dialog.add(saveBtn, gbc);

        dialog.setVisible(true);
    }
}

