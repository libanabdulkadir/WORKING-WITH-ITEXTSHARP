package task10;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;

public class MainApp {
    private JFrame frame;
    private JTextField receiptField, studentField, courseField, amountField;
    private JComboBox<String> paymentMethodBox;
    private JTable table;
    private DefaultTableModel tableModel;
    private DatabaseManager dbManager;
    private boolean isDarkMode = false;

    public MainApp() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        dbManager = new DatabaseManager();
        frame = new JFrame("Student Payment System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());

        // Dark Mode Toggle Button
        JToggleButton darkModeToggle = new JToggleButton("Dark Mode");
        darkModeToggle.addActionListener(e -> toggleDarkMode(darkModeToggle));

        // Panel for the form (left side)
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Enter Payment Details"));

        receiptField = new JTextField();
        studentField = new JTextField();
        courseField = new JTextField();
        amountField = new JTextField();
        paymentMethodBox = new JComboBox<>(new String[]{"Cash", "Card", "Bank Transfer"});

        formPanel.add(new JLabel("Receipt No:"));
        formPanel.add(receiptField);
        formPanel.add(new JLabel("Student Name:"));
        formPanel.add(studentField);
        formPanel.add(new JLabel("Payment Method:"));
        formPanel.add(paymentMethodBox);
        formPanel.add(new JLabel("Course Token:"));
        formPanel.add(courseField);
        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amountField);

        // Panel for buttons (below the form)
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        JButton loadButton = new JButton("Load Data");
        JButton exportButton = new JButton("Export to PDF");

        saveButton.setPreferredSize(new Dimension(120, 30));
        loadButton.setPreferredSize(new Dimension(120, 30));
        exportButton.setPreferredSize(new Dimension(120, 30));

        saveButton.addActionListener(e -> saveData());
        loadButton.addActionListener(e -> loadData());
        exportButton.addActionListener(e -> exportToPDF());

        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(darkModeToggle);

        // Panel for the table (right side)
        tableModel = new DefaultTableModel(new String[]{"Receipt No", "Student", "Payment Method", "Course", "Amount"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Payment Records"));

        // SplitPane for two columns: form (left) and table (right)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, tableScrollPane);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.4);

        // Add the panels to the frame
        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void toggleDarkMode(JToggleButton toggleButton) {
        try {
            if (isDarkMode) {
                UIManager.setLookAndFeel(new FlatLightLaf());
                toggleButton.setText(" Dark Mode");
            } else {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                toggleButton.setText(" Light Mode");
            }
            // Update the UI
            SwingUtilities.updateComponentTreeUI(frame);
            isDarkMode = !isDarkMode;
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        String receipt = receiptField.getText();
        String student = studentField.getText();
        String paymentMethod = (String) paymentMethodBox.getSelectedItem();
        String course = courseField.getText();
        String amount = amountField.getText();

        // Validation: Check if any field is empty
        if (receipt.isEmpty() || student.isEmpty() || paymentMethod.isEmpty() || course.isEmpty() || amount.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
            return;  // Prevent saving if any field is empty
        }

        dbManager.insertData(receipt, student, paymentMethod, course, amount);

        // Clear form fields after saving
        receiptField.setText("");
        studentField.setText("");
        courseField.setText("");
        amountField.setText("");
        paymentMethodBox.setSelectedIndex(0);

        JOptionPane.showMessageDialog(frame, "Data Saved Successfully!");
    }

    private void loadData() {
        tableModel.setRowCount(0); // Clear existing rows
        ResultSet rs = dbManager.getData();
        try {
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getString("receipt_no"),
                        rs.getString("student_name"),
                        rs.getString("payment_method"),
                        rs.getString("course_token"),
                        rs.getString("amount")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void exportToPDF() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(frame, "No data to export!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));

        if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.getName().endsWith(".pdf")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".pdf");
            }

            // Generate PDF using iText (same code as before)...
            // Your code here...
        }
    }

    public static void main(String[] args) {
        new MainApp();
    }
}
