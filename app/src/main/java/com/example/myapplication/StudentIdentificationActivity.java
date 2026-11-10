package com.example.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StudentIdentificationActivity extends AppCompatActivity {

    private EditText etStudentId;
    private TextView tvStudentInfo;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_identification);

        databaseHelper = new DatabaseHelper(this);

        etStudentId = findViewById(R.id.etStudentId);
        tvStudentInfo = findViewById(R.id.tvStudentInfo);

        Button btnSearchStudent = findViewById(R.id.btnSearchStudent);
        Button btnBack = findViewById(R.id.btnBack);

        btnSearchStudent.setOnClickListener(v -> searchStudent());
        btnBack.setOnClickListener(v -> finish());
    }

    private void searchStudent() {
        String studentId = etStudentId.getText().toString().trim();

        if (studentId.isEmpty()) {
            etStudentId.setError("Enter a student ID");
            etStudentId.requestFocus();
            return;
        }

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        try (Cursor cursor = db.query(
                "students",
                new String[]{
                        "student_id",
                        "first_name",
                        "last_name",
                        "grade",
                        "section",
                        "gender",
                        "age",
                        "contact"
                },
                "student_id = ?",
                new String[]{studentId},
                null,
                null,
                null
        )) {
            if (cursor.moveToFirst()) {
                String id = cursor.getString(
                        cursor.getColumnIndexOrThrow("student_id"));
                String firstName = cursor.getString(
                        cursor.getColumnIndexOrThrow("first_name"));
                String lastName = cursor.getString(
                        cursor.getColumnIndexOrThrow("last_name"));
                String grade = cursor.getString(
                        cursor.getColumnIndexOrThrow("grade"));
                String section = cursor.getString(
                        cursor.getColumnIndexOrThrow("section"));
                String gender = cursor.getString(
                        cursor.getColumnIndexOrThrow("gender"));
                String age = cursor.getString(
                        cursor.getColumnIndexOrThrow("age"));
                String contact = cursor.getString(
                        cursor.getColumnIndexOrThrow("contact"));

                String fullName = ((firstName == null ? "" : firstName)
                        + " " + (lastName == null ? "" : lastName)).trim();

                tvStudentInfo.setText(
                        "STUDENT INFORMATION\n\n" +
                                "Student ID: " + showValue(id) + "\n\n" +
                                "Full Name: " + showValue(fullName) + "\n\n" +
                                "Grade: " + showValue(grade) + "\n\n" +
                                "Section: " + showValue(section) + "\n\n" +
                                "Gender: " + showValue(gender) + "\n\n" +
                                "Age: " + showValue(age) + "\n\n" +
                                "Contact: " + showValue(contact)
                );
            } else {
                tvStudentInfo.setText("No student found.");
                Toast.makeText(this, "Student ID not found.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String showValue(String value) {
        return value == null || value.trim().isEmpty()
                ? "Not provided" : value;
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
