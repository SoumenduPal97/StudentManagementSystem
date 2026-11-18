

package com.studentmanagement.ui;

import com.studentmanagement.util.CsvExporter;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import com.studentmanagement.model.Admin;
import com.studentmanagement.dao.StudentDAO;
import com.studentmanagement.dao.AdminDAO;
import com.studentmanagement.model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class StudentManagementGUI extends JFrame {

    private final StudentDAO studentDAO = new StudentDAO();
    private final AdminDAO adminDAO = new AdminDAO();
    private final Admin currentAdmin;

    private final JTextField rollField = new JTextField(15);
    private final JTextField nameField = new JTextField(15);
    private final JTextField emailField = new JTextField(15);
    private final JTextField phoneField = new JTextField(15);
    private final JTextField courseField = new JTextField(15);
    private final JTextField marksField = new JTextField(15);
    private final JTextField searchField = new JTextField(25);

private final JTextField courseFilterField = new JTextField(10);

private final JTextField minMarksField = new JTextField(5);

private final JTextField maxMarksField = new JTextField(5);

private final JComboBox<String> sortByCombo =
        new JComboBox<>(new String[]{
                "Roll Number", "Name", "Marks", "Course"
        });

private final JComboBox<String> sortDirectionCombo =
        new JComboBox<>(new String[]{
                "Ascending", "Descending"
        });


    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new String[]{
                            "Roll Number", "Name", "Email",
                            "Phone", "Course", "Marks"
                    }, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable studentTable = new JTable(tableModel);

    public StudentManagementGUI(Admin admin) {

    if (admin == null) {
        throw new IllegalArgumentException(
                "Authenticated administrator required."
        );
    }

    this.currentAdmin = admin;

        setTitle("Student Management System - Version 2.0");
        setSize(1100, 720);
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 247, 250));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        configureTable();
        refreshStudents();
    }

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(35, 47, 62));
        header.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel title = new JLabel("STUDENT MANAGEMENT SYSTEM 2.0");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel subtitle = new JLabel(
                "Manage student profiles and academic information"
        );
        subtitle.setForeground(Color.LIGHT_GRAY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.WEST);

        return header;
    }

    private JPanel createMainPanel() {

        JPanel mainPanel = new JPanel(new BorderLayout(12, 15));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 20, 20)
        );

        JPanel topPanel = new JPanel(new BorderLayout(10, 12));
        topPanel.setOpaque(false);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 12));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(
                BorderFactory.createTitledBorder("Student Information")
        );

        formPanel.add(createFieldPanel("Roll Number", rollField));
        formPanel.add(createFieldPanel("Student Name", nameField));
        formPanel.add(createFieldPanel("Email Address", emailField));
        formPanel.add(createFieldPanel("Phone Number", phoneField));
        formPanel.add(createFieldPanel("Course", courseField));
        formPanel.add(createFieldPanel("Marks", marksField));

       JPanel buttonPanel = new JPanel(new GridLayout(0, 4, 10, 10));
       
    buttonPanel.setOpaque(false);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        

        buttonPanel.setOpaque(false);

        JButton addButton = new JButton("Add Student");
        JButton updateButton = new JButton("Update Student");
        JButton deleteButton = new JButton("Delete Student");
        JButton searchButton = new JButton("Advanced Search");
        JButton refreshButton = new JButton("Show All");
        JButton clearButton = new JButton("Clear");
        JButton changePasswordButton = new JButton("Change Password");
        JButton exportButton = new JButton("Export CSV");
        JButton logoutButton = new JButton("Logout");

        styleButton(addButton, new Color(46, 125, 50));
        styleButton(updateButton, new Color(30, 136, 229));
        styleButton(deleteButton, new Color(198, 40, 40));
        styleButton(searchButton, new Color(123, 77, 180));
        styleButton(refreshButton, new Color(90, 100, 110));
        styleButton(clearButton, new Color(100, 100, 100));
        styleButton(changePasswordButton, new Color(0, 121, 107));
        styleButton(exportButton, new Color(2, 119, 189));
        styleButton(logoutButton, new Color(183, 28, 28));

        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        searchButton.addActionListener(e -> searchStudent());
        refreshButton.addActionListener(e -> refreshStudents());
        clearButton.addActionListener(e -> clearFields());
        changePasswordButton.addActionListener(e -> changePassword());
        exportButton.addActionListener(e -> exportStudents());
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(changePasswordButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(logoutButton);

        
JPanel searchPanel = new JPanel(new BorderLayout(10, 5));
searchPanel.setOpaque(false);

JLabel searchLabel = new JLabel(
        "Search by Roll Number, Name, Email, Phone or Course:"
);

searchLabel.setFont(new Font("Arial", Font.BOLD, 13));

searchPanel.add(searchLabel, BorderLayout.WEST);
searchPanel.add(searchField, BorderLayout.CENTER);

JPanel filterPanel = new JPanel(
        new FlowLayout(FlowLayout.LEFT, 8, 5)
);

filterPanel.setOpaque(false);

JButton applyFiltersButton = new JButton("Apply Filters");
JButton resetFiltersButton = new JButton("Reset Filters");

styleButton(applyFiltersButton, new Color(46, 125, 50));
styleButton(resetFiltersButton, new Color(90, 100, 110));

filterPanel.add(new JLabel("Course:"));
filterPanel.add(courseFilterField);

filterPanel.add(new JLabel("Min Marks:"));
filterPanel.add(minMarksField);

filterPanel.add(new JLabel("Max Marks:"));
filterPanel.add(maxMarksField);

filterPanel.add(new JLabel("Sort By:"));
filterPanel.add(sortByCombo);

filterPanel.add(new JLabel("Direction:"));
filterPanel.add(sortDirectionCombo);

filterPanel.add(applyFiltersButton);
filterPanel.add(resetFiltersButton);

applyFiltersButton.addActionListener(
        e -> applyFiltersAndSorting()
);

resetFiltersButton.addActionListener(
        e -> resetFilters()
);

searchPanel.add(filterPanel, BorderLayout.SOUTH);


topPanel.add(searchPanel, BorderLayout.NORTH);

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

    private void styleButton(JButton button, Color color) {

        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void configureTable() {

        studentTable.setRowHeight(27);
        studentTable.setFont(new Font("Arial", Font.PLAIN, 13));
        studentTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        studentTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        studentTable.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                int row = studentTable.getSelectedRow();

                if (row >= 0) {
                    rollField.setText(getCellText(row, 0));
                    nameField.setText(getCellText(row, 1));
                    emailField.setText(getCellText(row, 2));
                    phoneField.setText(getCellText(row, 3));
                    courseField.setText(getCellText(row, 4));
                    marksField.setText(getCellText(row, 5));
                }
            }
        });
    }

    private String getCellText(int row, int column) {

        Object value = tableModel.getValueAt(row, column);

        return value == null ? "" : value.toString();
    }

    private Student getStudentFromForm() {

        String roll = rollField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String course = courseField.getText().trim();
        String marksText = marksField.getText().trim();

        if (roll.isEmpty() || name.isEmpty()
                || email.isEmpty() || phone.isEmpty()
                || course.isEmpty() || marksText.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Please fill in all fields."
            );

            return null;
        }

        String emailRegex =
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (!email.matches(emailRegex)) {

            JOptionPane.showMessageDialog(
                    this, "Please enter a valid email address."
            );

            return null;
        }

        if (!phone.matches("\\+?[0-9]{10,15}")) {

            JOptionPane.showMessageDialog(
                    this, "Enter a valid phone number (10-15 digits)."
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

            return new Student(
                    roll, name, email, phone, course, marks
            );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this, "Please enter valid numerical marks."
            );

            return null;
        }
    }

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

    private void refreshStudents() {

        try {

            List<Student> students = studentDAO.getAllStudents();

            tableModel.setRowCount(0);

            for (Student student : students) {

                tableModel.addRow(new Object[]{
                        student.getRollNumber(),
                        student.getName(),
                        student.getEmail(),
                        student.getPhone(),
                        student.getCourse(),
                        String.format("%.2f", student.getMarks())
                });
            }

        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    
private void searchStudent() {

    String keyword = searchField.getText().trim();

    if (keyword.isEmpty()) {

        JOptionPane.showMessageDialog(
                this,
                "Enter something to search."
        );

        return;
    }

    try {

        List<Student> students =
                studentDAO.searchStudents(keyword);

        tableModel.setRowCount(0);

        for (Student student : students) {

            tableModel.addRow(new Object[]{
                    student.getRollNumber(),
                    student.getName(),
                    student.getEmail(),
                    student.getPhone(),
                    student.getCourse(),
                    String.format("%.2f", student.getMarks())
            });
        }

        if (students.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "No matching students found."
            );

        }

    } catch (SQLException e) {

        showDatabaseError(e);
    }
}

private void applyFiltersAndSorting() {

    String course = courseFilterField.getText().trim();

    String minText = minMarksField.getText().trim();
    String maxText = maxMarksField.getText().trim();

    Double minMarks = null;
    Double maxMarks = null;

    try {

        if (!minText.isEmpty()) {
            minMarks = Double.parseDouble(minText);
        }

        if (!maxText.isEmpty()) {
            maxMarks = Double.parseDouble(maxText);
        }

    } catch (NumberFormatException e) {

        JOptionPane.showMessageDialog(
                this,
                "Please enter valid numerical marks."
        );

        return;
    }

    if (minMarks != null &&
            (!Double.isFinite(minMarks)
                    || minMarks < 0 || minMarks > 100)) {

        JOptionPane.showMessageDialog(
                this,
                "Minimum marks must be between 0 and 100."
        );

        return;
    }

    if (maxMarks != null &&
            (!Double.isFinite(maxMarks)
                    || maxMarks < 0 || maxMarks > 100)) {

        JOptionPane.showMessageDialog(
                this,
                "Maximum marks must be between 0 and 100."
        );

        return;
    }

    if (minMarks != null && maxMarks != null
            && minMarks > maxMarks) {

        JOptionPane.showMessageDialog(
                this,
                "Minimum marks cannot exceed maximum marks."
        );

        return;
    }

    String sortBy;

    switch ((String) sortByCombo.getSelectedItem()) {

        case "Name":
            sortBy = "name";
            break;

        case "Marks":
            sortBy = "marks";
            break;

        case "Course":
            sortBy = "course";
            break;

        default:
            sortBy = "roll_number";
    }

    String direction =
            "Descending".equals(sortDirectionCombo.getSelectedItem())
                    ? "DESC" : "ASC";

    try {

        List<Student> students =
                studentDAO.filterAndSortStudents(
                        course,
                        minMarks,
                        maxMarks,
                        sortBy,
                        direction
                );

        tableModel.setRowCount(0);

        for (Student student : students) {

            tableModel.addRow(new Object[]{
                    student.getRollNumber(),
                    student.getName(),
                    student.getEmail(),
                    student.getPhone(),
                    student.getCourse(),
                    String.format("%.2f", student.getMarks())
            });
        }

        if (students.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "No matching students found."
            );
        }

    } catch (SQLException e) {

        showDatabaseError(e);
    }
}

private void resetFilters() {

    courseFilterField.setText("");
    minMarksField.setText("");
    maxMarksField.setText("");

    sortByCombo.setSelectedItem("Roll Number");
    sortDirectionCombo.setSelectedItem("Ascending");

    refreshStudents();
}




    private void updateStudent() {

        Student student = getStudentFromForm();

        if (student == null) {
            return;
        }

        try {

            boolean updated = studentDAO.updateStudent(
                    student.getRollNumber(),
                    student.getName(),
                    student.getEmail(),
                    student.getPhone(),
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

    private void fillFields(Student student) {

        rollField.setText(student.getRollNumber());
        nameField.setText(student.getName());
        emailField.setText(
                student.getEmail() == null ? "" : student.getEmail()
        );
        phoneField.setText(
                student.getPhone() == null ? "" : student.getPhone()
        );
        courseField.setText(student.getCourse());
        marksField.setText(String.valueOf(student.getMarks()));
    }

    private void exportStudents() {

        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle("Export Student Records");

        chooser.setSelectedFile(
                new File("student-records.csv")
        );

        int result = chooser.showSaveDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File destination = chooser.getSelectedFile();

        if (!destination.getName().toLowerCase().endsWith(".csv")) {

            destination = new File(
                    destination.getParentFile(),
                    destination.getName() + ".csv"
            );
        }

        if (destination.exists()) {

            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "This file already exists. Do you want to replace it?",
                    "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {

            List<Student> students = studentDAO.getAllStudents();

            CsvExporter.exportStudents(
                    students,
                    destination.toPath()
            );

            JOptionPane.showMessageDialog(
                    this,
                    students.size() + " student records exported successfully!\n"
                            + "Location: " + destination.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException | IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Export failed: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


private void changePassword() {

    JPasswordField currentPasswordField =
            new JPasswordField(18);

    JPasswordField newPasswordField =
            new JPasswordField(18);

    JPasswordField confirmPasswordField =
            new JPasswordField(18);

    JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));

    panel.add(new JLabel("Current Password:"));
    panel.add(currentPasswordField);

    panel.add(new JLabel("New Password (minimum 12 characters):"));
    panel.add(newPasswordField);

    panel.add(new JLabel("Confirm New Password:"));
    panel.add(confirmPasswordField);

    int choice = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Change Administrator Password",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (choice != JOptionPane.OK_OPTION) {
        return;
    }

    char[] currentPassword = currentPasswordField.getPassword();
    char[] newPassword = newPasswordField.getPassword();
    char[] confirmPassword = confirmPasswordField.getPassword();

    try {

        if (currentPassword.length == 0
                || newPassword.length == 0
                || confirmPassword.length == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all password fields."
            );

            return;
        }

        if (newPassword.length < 12) {

            JOptionPane.showMessageDialog(
                    this,
                    "New password must contain at least 12 characters."
            );

            return;
        }

        if (!Arrays.equals(newPassword, confirmPassword)) {

            JOptionPane.showMessageDialog(
                    this,
                    "New passwords do not match."
            );

            return;
        }

        boolean changed = adminDAO.changePassword(
                currentAdmin.getUsername(),
                currentPassword,
                newPassword
        );

        if (changed) {

            JOptionPane.showMessageDialog(
                    this,
                    "Password changed successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Current password is incorrect.",
                    "Password Change Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }

    } catch (IllegalArgumentException e) {

        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Invalid Password",
                JOptionPane.WARNING_MESSAGE
        );

    } catch (SQLException | GeneralSecurityException e) {

        JOptionPane.showMessageDialog(
                this,
                "Unable to change password: " + e.getMessage(),
                "System Error",
                JOptionPane.ERROR_MESSAGE
        );

    } finally {

        Arrays.fill(currentPassword, '\0');
        Arrays.fill(newPassword, '\0');
        Arrays.fill(confirmPassword, '\0');

        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }
}


private void logout() {

    int confirmation = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to log out?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
    );

    if (confirmation == JOptionPane.YES_OPTION) {

        dispose();

        SwingUtilities.invokeLater(() -> {

            AdminLogin login = new AdminLogin();
            login.setVisible(true);

        });
    }
}

    private void clearFields() {

        rollField.setText("");
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        courseField.setText("");
        marksField.setText("");
        searchField.setText("");

        studentTable.clearSelection();
    }

    private void showDatabaseError(SQLException e) {

        JOptionPane.showMessageDialog(
                this,
                "Database Error: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

}
