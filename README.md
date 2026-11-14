# Student Management System

A desktop-based Student Management System developed using Java, MySQL, JDBC and Java Swing. This application allows users to manage student records through an easy-to-use graphical interface.

## Features

- Add new student records
- View all student records
- Search students using roll number
- Update student information
- Delete student records
- Store student data permanently using MySQL
- Validate user inputs
- User-friendly graphical interface built with Java Swing

## Technologies Used

- Java
- Java Swing
- MySQL
- JDBC
- MySQL Connector/J
- Git and GitHub
- Visual Studio Code

## Project Structure

```text
StudentManagementSystem/
│
├── src/
│   └── com/studentmanagement/
│       ├── model/
│       │   └── Student.java
│       ├── dao/
│       │   └── StudentDAO.java
│       ├── database/
│       │   └── DatabaseConnection.java
│       └── ui/
│           ├── Main.java
│           └── StudentManagementGUI.java
│
├── lib/
│   └── mysql-connector-j-26.10.0.jar
│
├── out/
├── db.properties.example
├── .gitignore
└── README.md
```

## Requirements

- JDK installed
- MySQL Server
- MySQL Workbench (optional, for database management)
- MySQL Connector/J

## Database Setup

1. Create a database named `student_management`.
2. Create the `students` table using the following SQL:

```sql
CREATE TABLE students (
    roll_number VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    course VARCHAR(100) NOT NULL,
    marks DECIMAL(5,2) NOT NULL,
    CHECK (marks >= 0 AND marks <= 100)
);
```

3. Create a MySQL user named `sms_app` and grant it SELECT, INSERT, UPDATE and DELETE permissions on the database.
4. Copy `db.properties.example` to a new file named `db.properties`.
5. Enter your own database credentials in `db.properties`.

## Configuration

Configure `db.properties` in the project root:

```properties
db.url=jdbc:mysql://localhost:3306/student_management
db.username=sms_app
db.password=YOUR_PASSWORD
```

Replace `YOUR_PASSWORD` with your MySQL user's password.

Never upload your actual `db.properties` file to GitHub.

## Compile the Project

Open the terminal in the project root directory and execute:

```powershell
javac -cp "lib\mysql-connector-j-26.10.0.jar" -d out src\com\studentmanagement\model\Student.java src\com\studentmanagement\database\DatabaseConnection.java src\com\studentmanagement\dao\StudentDAO.java src\com\studentmanagement\ui\Main.java src\com\studentmanagement\ui\StudentManagementGUI.java
```

## Run the Application

To start the graphical application:

```powershell
java -cp "out;lib\mysql-connector-j-26.10.0.jar" com.studentmanagement.ui.StudentManagementGUI
```

## Author

**Soumendu Pal**

GitHub: https://github.com/SoumenduPal97

## License

This project is created for educational and placement preparation purposes.


## Application Screenshots

![Student Management Dashboard](screenshots/student-management-dashboard.png)
