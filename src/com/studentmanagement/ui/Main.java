
package com.studentmanagement.ui;

import com.studentmanagement.dao.StudentDAO;
import com.studentmanagement.model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final StudentDAO studentDAO = new StudentDAO();

    public static void runConsole(String[] args) {

        boolean running = true;

        while (running) {

            System.out.println("\n================================");
            System.out.println("     STUDENT MANAGEMENT SYSTEM 2.0");
            System.out.println("================================");

            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. Search Student");
            System.out.println("4. Update Student");
            System.out.println("5. Delete Student");
            System.out.println("6. Exit");

            System.out.print("\nEnter your choice: ");

            int choice = readInt();

            try {

                switch (choice) {

                    case 1:
                        addStudent();
                        break;

                    case 2:
                        viewStudents();
                        break;

                    case 3:
                        searchStudent();
                        break;

                    case 4:
                        updateStudent();
                        break;

                    case 5:
                        deleteStudent();
                        break;

                    case 6:
                        System.out.println("Thank you for using the system!");
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }

            } catch (SQLException e) {

                System.out.println("Database operation failed!");
                System.out.println("Error: " + e.getMessage());
            }
        }

        sc.close();
    }

    private static void addStudent() throws SQLException {

        System.out.println("\n--- Add Student ---");

        System.out.print("Enter Roll Number: ");
        String roll = readNonEmpty();

        if (studentDAO.searchStudent(roll) != null) {
            System.out.println("This roll number already exists.");
            return;
        }

        System.out.print("Enter Student Name: ");
        String name = readNonEmpty();

        String email = readEmail();
        String phone = readPhone();

        System.out.print("Enter Course: ");
        String course = readNonEmpty();

        double marks = readMarks();

        Student student = new Student(
                roll, name, email, phone, course, marks
        );

        if (studentDAO.addStudent(student)) {
            System.out.println("Student added successfully!");
        } else {
            System.out.println("Unable to add student.");
        }
    }

    private static void viewStudents() throws SQLException {

        System.out.println("\n--- Student Records ---");

        List<Student> students = studentDAO.getAllStudents();

        if (students.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }

        System.out.printf(
                "%-12s %-18s %-25s %-15s %-18s %-8s%n",
                "Roll No.", "Name", "Email", "Phone", "Course", "Marks"
        );

        System.out.println(
                "-------------------------------------------------------------------------------------------------------------"
        );

        for (Student student : students) {

            System.out.printf(
                    "%-12s %-18s %-25s %-15s %-18s %-8.2f%n",
                    student.getRollNumber(),
                    student.getName(),
                    student.getEmail() == null ? "" : student.getEmail(),
                    student.getPhone() == null ? "" : student.getPhone(),
                    student.getCourse(),
                    student.getMarks()
            );
        }
    }

    private static void searchStudent() throws SQLException {

        System.out.print("\nEnter Roll Number to Search: ");
        String roll = readNonEmpty();

        Student student = studentDAO.searchStudent(roll);

        if (student == null) {
            System.out.println("Student not found.");
        } else {
            System.out.println("\nStudent Found!");
            showDetails(student);
        }
    }

    private static void updateStudent() throws SQLException {

        System.out.print("\nEnter Roll Number to Update: ");
        String roll = readNonEmpty();

        Student student = studentDAO.searchStudent(roll);

        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.print("Enter New Name: ");
        String name = readNonEmpty();

        String email = readEmail();
        String phone = readPhone();

        System.out.print("Enter New Course: ");
        String course = readNonEmpty();

        double marks = readMarks();

        boolean updated = studentDAO.updateStudent(
                roll, name, email, phone, course, marks
        );

        if (updated) {
            System.out.println("Student updated successfully!");
        } else {
            System.out.println("Unable to update student.");
        }
    }

    private static void deleteStudent() throws SQLException {

        System.out.print("\nEnter Roll Number to Delete: ");
        String roll = readNonEmpty();

        boolean deleted = studentDAO.deleteStudent(roll);

        if (deleted) {
            System.out.println("Student deleted successfully!");
        } else {
            System.out.println("Student not found.");
        }
    }

    private static void showDetails(Student student) {

        System.out.println("Roll Number: " + student.getRollNumber());
        System.out.println("Name: " + student.getName());
        System.out.println("Email: " + student.getEmail());
        System.out.println("Phone: " + student.getPhone());
        System.out.println("Course: " + student.getCourse());
        System.out.println("Marks: " + student.getMarks());
    }

    private static int readInt() {

        while (true) {

            try {
                return Integer.parseInt(sc.nextLine().trim());

            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private static String readNonEmpty() {

        while (true) {

            String value = sc.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.print("This field cannot be empty. Try again: ");
        }
    }

    private static String readEmail() {

        while (true) {

            System.out.print("Enter Email Address: ");
            String email = readNonEmpty();

            if (email.matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return email;
            }

            System.out.println("Please enter a valid email address.");
        }
    }

    private static String readPhone() {

        while (true) {

            System.out.print("Enter Phone Number: ");
            String phone = readNonEmpty();

            if (phone.matches("\\+?[0-9]{10,15}")) {
                return phone;
            }

            System.out.println(
                    "Enter a valid phone number (10-15 digits)."
            );
        }
    }

    private static double readMarks() {

        while (true) {

            System.out.print("Enter Marks: ");

            try {

                double marks = Double.parseDouble(
                        sc.nextLine().trim()
                );

                if (Double.isFinite(marks) && marks >= 0 && marks <= 100) {
                    return marks;
                }

                System.out.println("Enter marks between 0 and 100.");

            } catch (NumberFormatException e) {

                System.out.println("Please enter valid numerical marks.");
            }
        }
    }
}
