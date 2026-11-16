
package com.studentmanagement.dao;

import com.studentmanagement.model.Student;
import com.studentmanagement.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public boolean addStudent(Student student) throws SQLException {

        String sql = "INSERT INTO students "
                + "(roll_number, name, email, phone, course, marks) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, student.getRollNumber());
            ps.setString(2, student.getName());
            ps.setString(3, student.getEmail());
            ps.setString(4, student.getPhone());
            ps.setString(5, student.getCourse());
            ps.setDouble(6, student.getMarks());

            return ps.executeUpdate() > 0;
        }
    }

    public List<Student> getAllStudents() throws SQLException {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM students ORDER BY roll_number";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Student student = new Student(
                        rs.getString("roll_number"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("course"),
                        rs.getDouble("marks")
                );

                students.add(student);
            }
        }

        return students;
    }

    public Student searchStudent(String rollNumber) throws SQLException {

        String sql = "SELECT * FROM students WHERE roll_number = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, rollNumber);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Student(
                            rs.getString("roll_number"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("course"),
                            rs.getDouble("marks")
                    );
                }
            }
        }

        return null;
    }

    public boolean updateStudent(String rollNumber,
                                 String name,
                                 String email,
                                 String phone,
                                 String course,
                                 double marks) throws SQLException {

        String sql = "UPDATE students SET name = ?, email = ?, "
                + "phone = ?, course = ?, marks = ? "
                + "WHERE roll_number = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, course);
            ps.setDouble(5, marks);
            ps.setString(6, rollNumber);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteStudent(String rollNumber) throws SQLException {

        String sql = "DELETE FROM students WHERE roll_number = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, rollNumber);

            return ps.executeUpdate() > 0;
        }
    }
}
