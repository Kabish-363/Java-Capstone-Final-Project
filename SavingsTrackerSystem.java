import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

// Main Class for the Savings Tracker System
public class SavingsTrackerSystem extends JFrame {
    private ArrayList<SavingsGoal> goalsList = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable goalsTable;
    private JTextField nameField, targetField, contributionField;

    public SavingsTrackerSystem() {
        setTitle("Financial Progress Tracker");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- TOP PANEL: Input Area ---
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add New Savings Goal"));
        
        inputPanel.add(new JLabel("Goal Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Target Amount ($):"));
        targetField = new JTextField();
        inputPanel.add(targetField);

        JButton addBtn = new JButton("Create Goal");
        addBtn.addActionListener(e -> addGoal());
        inputPanel.add(addBtn);
        
        add(inputPanel, BorderLayout.NORTH);

        // --- CENTER PANEL: Progress Table ---
        String[] columns = {"Goal Name", "Target", "Saved", "Progress %", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        goalsTable = new JTable(tableModel);
        add(new JScrollPane(goalsTable), BorderLayout.CENTER);

        // --- BOTTOM PANEL: Update Savings ---
        JPanel progressPanel = new JPanel(new FlowLayout());
        progressPanel.setBorder(BorderFactory.createTitledBorder("Log Contribution"));
        
        progressPanel.add(new JLabel("Amount ($):"));
        contributionField = new JTextField(10);
        progressPanel.add(contributionField);

        JButton updateBtn = new JButton("Add to Selected Goal");
        updateBtn.addActionListener(e -> updateGoalProgress());
        progressPanel.add(updateBtn);

        add(progressPanel, BorderLayout.SOUTH);
    }

    private void addGoal() {
        try {
            String name = nameField.getText();
            double target = Double.parseDouble(targetField.getText());
            if (name.isEmpty()) throw new Exception();

            SavingsGoal newGoal = new SavingsGoal(name, target);
            goalsList.add(newGoal);
            refreshTable();
            
            nameField.setText("");
            targetField.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Name and Target Amount.");
        }
    }

    private void updateGoalProgress() {
        int selectedRow = goalsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a goal from the table first.");
            return;
        }

        try {
            double amount = Double.parseDouble(contributionField.getText());
            SavingsGoal goal = goalsList.get(selectedRow);
            goal.addSavings(amount);
            refreshTable();
            contributionField.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid numeric contribution amount.");
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (SavingsGoal goal : goalsList) {
            Object[] row = {
                goal.getName(),
                "$" + goal.getTarget(),
                "$" + goal.getCurrent(),
                String.format("%.1f%%", goal.getProgressPercent()),
                goal.getCurrent() >= goal.getTarget() ? "COMPLETED" : "IN PROGRESS"
            };
            tableModel.addRow(row);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SavingsTrackerSystem().setVisible(true));
    }

    // --- Inner Data Model Class ---
    class SavingsGoal {
        private String name;
        private double target;
        private double current;

        public SavingsGoal(String name, double target) {
            this.name = name;
            this.target = target;
            this.current = 0;
        }

        public void addSavings(double amount) { this.current += amount; }
        public String getName() { return name; }
        public double getTarget() { return target; }
        public double getCurrent() { return current; }
        public double getProgressPercent() { return (current / target) * 100; }
    }
}