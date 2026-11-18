
package com.studentmanagement.util;

import com.studentmanagement.model.Student;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class CsvExporter {

    private CsvExporter() {
        
    }

    public static void exportStudents(
            List<Student> students,
            Path destination
    ) throws IOException {

        try (BufferedWriter writer = Files.newBufferedWriter(
                destination,
                StandardCharsets.UTF_8
        )) {

            writer.write('\uFEFF');

            writeRow(
                    writer,
                    "Roll Number",
                    "Name",
                    "Email",
                    "Phone",
                    "Course",
                    "Marks"
            );

            for (Student student : students) {

                writeRow(
                        writer,
                        student.getRollNumber(),
                        student.getName(),
                        student.getEmail(),
                        student.getPhone(),
                        student.getCourse(),
                        String.valueOf(student.getMarks())
                );
            }
        }
    }

    private static void writeRow(
            BufferedWriter writer,
            String... values
    ) throws IOException {

        for (int i = 0; i < values.length; i++) {

            if (i > 0) {
                writer.write(',');
            }

            String value = values[i] == null ? "" : values[i];

            value = protectSpreadsheetFormula(value);

            writer.write('"');
            writer.write(value.replace("\"", "\"\""));
            writer.write('"');
        }

        writer.newLine();
    }

    private static String protectSpreadsheetFormula(String value) {

        int index = 0;

        while (index < value.length()
                && Character.isWhitespace(value.charAt(index))) {
            index++;
        }

        if (index < value.length()) {

            char first = value.charAt(index);

            if (first == '=' || first == '+'
                    || first == '-' || first == '@') {

                return "'" + value;
            }
        }

        return value;
    }
}

