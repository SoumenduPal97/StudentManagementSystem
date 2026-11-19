
package com.studentmanagement.util;

import com.studentmanagement.model.Student;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldCreateCSVFile() throws IOException {

        Path file = tempDir.resolve("students.csv");

        CsvExporter.exportStudents(
                Collections.emptyList(),
                file
        );

        assertTrue(Files.exists(file));
    }

    @Test
    void shouldExportStudentDetails() throws IOException {

        Path file = tempDir.resolve("students.csv");

        Student student = new Student(
                "101",
                "Rahul Sharma",
                "rahul@gmail.com",
                "9876543210",
                "B.Tech CSE",
                88.5
        );

        CsvExporter.exportStudents(
                Collections.singletonList(student),
                file
        );

        String csv = Files.readString(
                file, StandardCharsets.UTF_8
        );

        assertTrue(csv.contains("\"Roll Number\""));
        assertTrue(csv.contains("\"Name\""));
        assertTrue(csv.contains("\"Email\""));
        assertTrue(csv.contains("\"Phone\""));
        assertTrue(csv.contains("\"Course\""));
        assertTrue(csv.contains("\"Marks\""));

        assertTrue(csv.contains(
                "\"101\",\"Rahul Sharma\",\"rahul@gmail.com\","
                + "\"9876543210\",\"B.Tech CSE\",\"88.5\""
        ));
    }

    @Test
    void shouldEscapeSpecialCharacters() throws IOException {

        Path file = tempDir.resolve("students.csv");

        Student student = new Student(
                "102",
                "Doe, \"Jane\"",
                "jane@gmail.com",
                "9876543211",
                "B.Tech CSE",
                90.0
        );

        CsvExporter.exportStudents(
                Collections.singletonList(student),
                file
        );

        String csv = Files.readString(
                file, StandardCharsets.UTF_8
        );

        assertTrue(csv.contains("\"Doe, \"\"Jane\"\"\""));
    }

    @Test
    void shouldHandleNullValues() throws IOException {

        Path file = tempDir.resolve("students.csv");

        Student student = new Student(
                "103",
                "Amit",
                null,
                null,
                "B.Tech IT",
                75.0
        );

        CsvExporter.exportStudents(
                Collections.singletonList(student),
                file
        );

        String csv = Files.readString(
                file, StandardCharsets.UTF_8
        );

        assertTrue(csv.contains(
                "\"103\",\"Amit\",\"\",\"\",\"B.Tech IT\",\"75.0\""
        ));
    }

    @Test
    void shouldProtectAgainstFormulaInjection() throws IOException {

        Path file = tempDir.resolve("students.csv");

        Student student = new Student(
                "104",
                "=2+2",
                "test@gmail.com",
                "9876543212",
                "B.Tech CSE",
                80.0
        );

        CsvExporter.exportStudents(
                Collections.singletonList(student),
                file
        );

        String csv = Files.readString(
                file, StandardCharsets.UTF_8
        );

        assertTrue(csv.contains("\"'=2+2\""));
    }

    @Test
    void shouldExportOnlyHeadersWhenNoStudentsExist()
            throws IOException {

        Path file = tempDir.resolve("empty.csv");

        CsvExporter.exportStudents(
                Collections.emptyList(),
                file
        );

        String csv = Files.readString(
                file, StandardCharsets.UTF_8
        );

        assertTrue(csv.contains("\"Roll Number\""));
        assertFalse(csv.contains("\"101\""));
    }
}


