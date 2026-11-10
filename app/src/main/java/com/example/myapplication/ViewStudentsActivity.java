package com.example.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewStudentsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView tvStudentList;
    private TextView tvStudentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_students);

        tvStudentList = findViewById(R.id.tvStudentList);
        tvStudentCount = findViewById(R.id.tvStudentCount);

        Button btnBack = findViewById(R.id.btnBack);
        databaseHelper = new DatabaseHelper(this);

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadStudents();
        }
    }

    private void loadStudents() {
        StringBuilder records = new StringBuilder();
        int count = 0;

        try (Cursor cursor = databaseHelper.getAllStudents()) {
            int idIndex = cursor.getColumnIndex("student_id");
            int firstNameIndex = cursor.getColumnIndex("first_name");
            int lastNameIndex = cursor.getColumnIndex("last_name");
            int gradeIndex = cursor.getColumnIndex("grade");
            int sectionIndex = cursor.getColumnIndex("section");
            int ageIndex = cursor.getColumnIndex("age");
            int contactIndex = cursor.getColumnIndex("contact");

            while (cursor.moveToNext()) {
                String studentId = getValue(cursor, idIndex);
                String firstName = getValue(cursor, firstNameIndex);
                String lastName = getValue(cursor, lastNameIndex);
                String grade = getValue(cursor, gradeIndex);
                String section = getValue(cursor, sectionIndex);
                String age = getValue(cursor, ageIndex);
                String contact = getValue(cursor, contactIndex);

                String fullName = (firstName + " " + lastName).trim();
                if (fullName.isEmpty()) {
                    fullName = "Name not provided";
                }

                String gradeSection = (grade + " " + section).trim();
                if (gradeSection.isEmpty()) {
                    gradeSection = "Not provided";
                }

                if (age.isEmpty()) {
                    age = "Not provided";
                }

                if (contact.isEmpty()) {
                    contact = "Not provided";
                }

                count++;

                records.append("Student ").append(count).append("\n")
                        .append("ID: ").append(studentId).append("\n")
                        .append("Name: ").append(fullName).append("\n")
                        .append("Grade & Section: ")
                        .append(gradeSection).append("\n")
                        .append("Age: ").append(age).append("\n")
                        .append("Contact: ").append(contact).append("\n")
                        .append("------------------------------\n\n");
            }
        }

        tvStudentCount.setText("Total students: " + count);

        if (count == 0) {
            tvStudentList.setText("No students registered yet.");
        } else {
            tvStudentList.setText(records.toString());
        }
    }

    private String getValue(Cursor cursor, int columnIndex) {
        if (columnIndex < 0 || cursor.isNull(columnIndex)) {
            return "";
        }

        String value = cursor.getString(columnIndex);
        return value == null ? "" : value;
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
