package ui;

import db.Database;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomPanel {
    private Database db;
    private JPanel panel;
    private JTable table;
    private DefaultTableModel tableModel;

    public RoomPanel(Database db) {
        this.db = db;
        createPanel();
        loadData();
    }

    public JPanel getPanel() {
        return panel;
    }

    private void createPanel() {
        panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Номер", "Отделение", "Мест", "Свободна"};
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
                JOptionPane.showMessageDialog(this.panel, "Выберите палату!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            showDialog(id);
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this.panel, "Выберите палату!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this.panel, "Удалить палату?", "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                db.deleteRoom(id);
                loadData();
                JOptionPane.showMessageDialog(this.panel, "Палата удалена!");
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
        for (Room r : db.getAllRooms()) {
            String depName = "";
            for (Department d : db.getAllDepartments()) if (d.getId() == r.getDepartmentId()) depName = d.getName();
            tableModel.addRow(new Object[]{r.getId(), r.getRoomNumber(), depName, r.getBeds(), r.isFree() ? "Да" : "Нет"});
        }
    }

    private void showDialog(Integer roomId) {
        Room existing = null;
        if (roomId != null) {
            for (Room r : db.getAllRooms()) {
                if (r.getId() == roomId) {
                    existing = r;
                    break;
                }
            }
            if (existing == null) return;
        }

        final Room finalExisting = existing;

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panel),
                roomId == null ? "Добавление палаты" : "Редактирование палаты", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(panel);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField numberField = new JTextField(finalExisting != null ? String.valueOf(finalExisting.getRoomNumber()) : "", 20);
        JComboBox<Department> departmentCombo = new JComboBox<>();
        JTextField bedsField = new JTextField(finalExisting != null ? String.valueOf(finalExisting.getBeds()) : "", 20);
        JComboBox<String> freeCombo = new JComboBox<>(new String[]{"Да", "Нет"});

        List<Department> departments = db.getAllDepartments();
        for (Department d : departments) departmentCombo.addItem(d);

        if (finalExisting != null) {
            freeCombo.setSelectedItem(finalExisting.isFree() ? "Да" : "Нет");
            for (int i = 0; i < departments.size(); i++) {
                if (departments.get(i).getId() == finalExisting.getDepartmentId()) {
                    departmentCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Номер палаты:"), gbc);
        gbc.gridx = 1; dialog.add(numberField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Отделение:"), gbc);
        gbc.gridx = 1; dialog.add(departmentCombo, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Количество мест:"), gbc);
        gbc.gridx = 1; dialog.add(bedsField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; dialog.add(new JLabel("Свободна:"), gbc);
        gbc.gridx = 1; dialog.add(freeCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JButton saveBtn = new JButton(roomId == null ? "Сохранить" : "Обновить");
        saveBtn.addActionListener(e -> {
            Department dep = (Department) departmentCombo.getSelectedItem();
            if (numberField.getText().isEmpty() || dep == null) {
                JOptionPane.showMessageDialog(dialog, "Заполните номер и выберите отделение!");
                return;
            }
            int number = Integer.parseInt(numberField.getText());
            int beds = 1;
            try { beds = Integer.parseInt(bedsField.getText()); } catch (Exception ex) {}
            boolean isFree = freeCombo.getSelectedItem().equals("Да");
            if (roomId == null) {
                Room r = new Room(0, number, dep.getId(), beds, isFree);
                db.addRoom(r);
                JOptionPane.showMessageDialog(dialog, "Палата добавлена!");
            } else {
                Room r = new Room(roomId, number, dep.getId(), beds, isFree);
                db.updateRoom(r);
                JOptionPane.showMessageDialog(dialog, "Данные обновлены!");
            }
            dialog.dispose();
            loadData();
        });
        dialog.add(saveBtn, gbc);

        dialog.setVisible(true);
    }
}
