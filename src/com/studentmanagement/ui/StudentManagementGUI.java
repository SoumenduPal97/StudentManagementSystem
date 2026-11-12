
package com.studentmanagement.ui;

import com.studentmanagement.dao.StudentDAO;
import com.studentmanagement.model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;


public class StudentManagementGUI extends JFrame {

    private final StudentDAO studentDAO = new StudentDAO();

    private final JTextField rollField = new JTextField(15);
    private final JTextField nameField = new JTextField(15);
    private final JTextField courseField = new JTextField(15);
    private final JTextField marksField = new JTextField(15);

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new String[]{"Roll Number", "Name", "Course", "Marks"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable studentTable = new JTable(tableModel);

    public StudentManagementGUI() {

        setTitle("Student Management System");
        setSize(950, 650);
        setMinimumSize(new Dimension(750, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 247, 250));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        configureTable();

        // Load records from MySQL when the window opens
        refreshStudents();
    }

    // Header
    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(35, 47, 62));
        header.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel title = new JLabel("STUDENT MANAGEMENT SYSTEM");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 25));

        JLabel subtitle = new JLabel(
                "Manage student records using Java and MySQL"
        );
        subtitle.setForeground(Color.LIGHT_GRAY);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.WEST);

        return header;
    }

    // Main panel
    private JPanel createMainPanel() {

        JPanel mainPanel = new JPanel(new BorderLayout(12, 15));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 20, 20)
        );

        JPanel topPanel = new JPanel(new BorderLayout(10, 12));
        topPanel.setOpaque(false);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 15, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(
                BorderFactory.createTitledBorder("Student Information")
        );

        formPanel.add(createFieldPanel("Roll Number", rollField));
        formPanel.add(createFieldPanel("Student Name", nameField));
        formPanel.add(createFieldPanel("Course", courseField));
        formPanel.add(createFieldPanel("Marks", marksField));

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 5)
        );
        buttonPanel.setOpaque(false);

        JButton addButton = new JButton("Add Student");
        JButton updateButton = new JButton("Update Student");
        JButton deleteButton = new JButton("Delete Student");
        JButton searchButton = new JButton("Search Student");
        JButton refreshButton = new JButton("Show All");
        JButton clearButton = new JButton("Clear");

        styleButton(addButton, new Color(46, 125, 50));
        styleButton(updateButton, new Color(30, 136, 229));
        styleButton(deleteButton, new Color(198, 40, 40));
        styleButton(searchButton, new Color(123, 77, 180));
        styleButton(refreshButton, new Color(90, 100, 110));
        styleButton(clearButton, new Color(100, 100, 100));

        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        searchButton.addActionListener(e -> searchStudent());
        refreshButton.addActionListener(e -> refreshStudents());
        clearButton.addActionListener(e -> clearFields());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(clearButton);

        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(
                BorderFactory.createTitledBorder("Student Records")
        );

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    // Create input field panel
    private JPanel createFieldPanel(String label, JTextField field) {

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);

        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.BOLD, 13));

        field.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(jLabel, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    // Button styling
    private void styleButton(JButton button, Color color) {

        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Table configuration
    private void configureTable() {

        studentTable.setRowHeight(27);
        studentTable.setFont(new Font("Arial", Font.PLAIN, 13));
        studentTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        studentTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        studentTable.setSelectionBackground(
                new Color(187, 222, 251)
        );

        studentTable.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                int row = studentTable.getSelectedRow();

                if (row >= 0) {
                    rollField.setText(
                            tableModel.getValueAt(row, 0).toString()
                    );

                    nameField.setText(
                            tableModel.getValueAt(row, 1).toString()
                    );

                    courseField.setText(
                            tableModel.getValueAt(row, 2).toString()
                    );

                    marksField.setText(
                            tableModel.getValueAt(row, 3).toString()
                    );
                }
            }
        });
    }

    // Validate form and create Student object
    private Student getStudentFromForm() {

        String roll = rollField.getText().trim();
        String name = nameField.getText().trim();
        String course = courseField.getText().trim();
        String marksText = marksField.getText().trim();

        if (roll.isEmpty() || name.isEmpty()
                || course.isEmpty() || marksText.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Please fill in all fields."
            );

            return null;
        }

        try {

            double marks = Double.parseDouble(marksText);

            if (!Double.isFinite(marks) || marks < 0 || marks > 100) {

                JOptionPane.showMessageDialog(
                        this, "Marks must be between 0 and 100."
                );

                return null;
            }

            return new Student(roll, name, course, marks);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this, "Please enter valid numerical marks."
            );

            return null;
        }
    }

    // Add Student
    private void addStudent() {

        Student student = getStudentFromForm();

        if (student == null) {
            return;
        }

        try {

            if (studentDAO.searchStudent(
                    student.getRollNumber()) != null) {

                JOptionPane.showMessageDialog(
                        this, "This roll number already exists."
                );

                return;
            }

            if (studentDAO.addStudent(student)) {

                JOptionPane.showMessageDialog(
                        this, "Student added successfully!"
                );

                refreshStudents();
                clearFields();
            }

        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    // View All Students
    private void refreshStudents() {

        try {

            List<Student> students = studentDAO.getAllStudents();

            tableModel.setRowCount(0);

            for (Student student : students) {

                tableModel.addRow(new Object[]{
                        student.getRollNumber(),
                        student.getName(),
                        student.getCourse(),
                        String.format("%.2f", student.getMarks())
                });
            }

        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    // Search Student
    private void searchStudent() {

        String roll = rollField.getText().trim();

        if (roll.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Enter a roll number to search."
            );

            return;
        }

        try {

            Student student = studentDAO.searchStudent(roll);

            tableModel.setRowCount(0);

            if (student == null) {

                JOptionPane.showMessageDialog(
                        this, "Student not found."
                );

                return;
            }

            tableModel.addRow(new Object[]{
                    student.getRollNumber(),
                    student.getName(),
                    student.getCourse(),
                    String.format("%.2f", student.getMarks())
            });

            fillFields(student);

        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    // Update Student
    private void updateStudent() {

        Student student = getStudentFromForm();

        if (student == null) {
            return;
        }

        try {

            boolean updated = studentDAO.updateStudent(
                    student.getRollNumber(),
                    student.getName(),
                    student.getCourse(),
                    student.getMarks()
            );

            if (updated) {

                JOptionPane.showMessageDialog(
                        this, "Student updated successfully!"
                );

                refreshStudents();
                clearFields();

            } else {

                JOptionPane.showMessageDialog(
                        this, "Student not found."
                );
            }

        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    // Delete Student
    private void deleteStudent() {

        String roll = rollField.getText().trim();

        if (roll.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Enter a roll number to delete."
            );

            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this student?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            boolean deleted = studentDAO.deleteStudent(roll);

            if (deleted) {

                JOptionPane.showMessageDialog(
                        this, "Student deleted successfully!"
                );

                refreshStudents();
                clearFields();

            } else {

                JOptionPane.showMessageDialog(
                        this, "Student not found."
                );
            }

        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    // Fill input fields from selected student
    private void fillFields(Student student) {

        rollField.setText(student.getRollNumber());
        nameField.setText(student.getName());
        courseField.setText(student.getCourse());
        marksField.setText(String.valueOf(student.getMarks()));
    }

    // Clear all fields
    private void clearFields() {

        rollField.setText("");
        nameField.setText("");
        courseField.setText("");
        marksField.setText("");

        studentTable.clearSelection();
    }

    // Show database errors
    private void showDatabaseError(SQLException e) {

        JOptionPane.showMessageDialog(
                this,
                "Database Error: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // Application starting point
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            StudentManagementGUI gui = new StudentManagementGUI();
            gui.setVisible(true);

        });
    }
}
