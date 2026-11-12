
package com.studentmanagement.model; 

public class Student {

    private String rollNumber;
    private String name;
    private String course;
    private double marks;

    public Student(String rollNumber, String name, String course, double marks) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.course = course;
        this.marks = marks;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public String getName() {
        return name;
    }

    public String getCourse() {
        return course;
    }

    public double getMarks() {
        return marks;
    }

    public void updateDetails(String name, String course, double marks) {
        this.name = name;
        this.course = course;
        this.marks = marks;
    }
}

