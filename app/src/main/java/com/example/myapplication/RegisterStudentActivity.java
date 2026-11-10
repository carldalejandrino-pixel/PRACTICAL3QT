package com.example.myapplication;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterStudentActivity extends AppCompatActivity {

    private EditText etStudentId;
    private EditText etStudentName;
    private EditText etGradeSection;
    private EditText etAge;
    private EditText etContact;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        databaseHelper = new DatabaseHelper(this);

        etStudentId = findViewById(R.id.etStudentId);
        etStudentName = findViewById(R.id.etStudentName);
        etGradeSection = findViewById(R.id.etGradeSection);
        etAge = findViewById(R.id.etAge);
        etContact = findViewById(R.id.etContact);

        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnBack = findViewById(R.id.btnBack);

        btnRegister.setOnClickListener(v -> registerStudent());
        btnBack.setOnClickListener(v -> finish());
    }

    private void registerStudent() {
        String studentId = etStudentId.getText().toString().trim();
        String fullName = etStudentName.getText().toString().trim();
        String gradeSection = etGradeSection.getText().toString().trim();
        String ageText = etAge.getText().toString().trim();
        String contact = etContact.getText().toString().trim();

        if (studentId.isEmpty() || fullName.isEmpty()
                || gradeSection.isEmpty() || ageText.isEmpty()) {
            Toast.makeText(this,
                    "Please fill in all required fields.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String[] nameParts = fullName.split("\\s+", 2);

        if (nameParts.length < 2) {
            Toast.makeText(this,
                    "Please enter your first and last name.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int age;

        try {
            age = Integer.parseInt(ageText);

            if (age < 1 || age > 120) {
                Toast.makeText(this,
                        "Please enter a valid age.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "Age must be a number.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (databaseHelper.studentExists(studentId)) {
                Toast.makeText(this,
                        "Student ID already exists.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String[] gradeParts = gradeSection.split("\\s+", 2);
            String grade = gradeParts[0];
            String section = gradeParts.length > 1 ? gradeParts[1] : "";

            boolean success = databaseHelper.addStudent(
                    studentId,
                    nameParts[0],
                    nameParts[1],
                    grade,
                    section,
                    "",
                    String.valueOf(age),
                    contact
            );

            if (success) {
                Toast.makeText(this,
                        "Student registered successfully!",
                        Toast.LENGTH_SHORT).show();

                etStudentId.setText("");
                etStudentName.setText("");
                etGradeSection.setText("");
                etAge.setText("");
                etContact.setText("");
            } else {
                Toast.makeText(this,
                        "Registration failed.",
                        Toast.LENGTH_LONG).show();
            }

        } catch (SQLiteException e) {
            android.util.Log.e(
                    "StudentRegistration",
                    "Registration failed",
                    e
            );

            Toast.makeText(this,
                    "Database error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}
