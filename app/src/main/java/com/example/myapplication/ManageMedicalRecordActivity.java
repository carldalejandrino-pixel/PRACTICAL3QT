package com.example.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ManageMedicalRecordActivity extends AppCompatActivity {

    private EditText etStudentId;
    private EditText etAllergies;
    private EditText etConditions;
    private EditText etMedications;
    private EditText etMedicalNotes;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_medical_record);

        etStudentId = findViewById(R.id.etMedicalStudentId);
        etAllergies = findViewById(R.id.etAllergies);
        etConditions = findViewById(R.id.etConditions);
        etMedications = findViewById(R.id.etMedications);
        etMedicalNotes = findViewById(R.id.etMedicalNotes);

        Button btnLoad = findViewById(R.id.btnLoadMedicalRecord);
        Button btnSave = findViewById(R.id.btnSaveMedicalRecord);
        Button btnBack = findViewById(R.id.btnMedicalBack);

        databaseHelper = new DatabaseHelper(this);

        btnLoad.setOnClickListener(v -> loadMedicalRecord());
        btnSave.setOnClickListener(v -> saveMedicalRecord());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadMedicalRecord() {
        String studentId = etStudentId.getText().toString().trim();

        if (studentId.isEmpty()) {
            etStudentId.setError("Enter a student ID");
            etStudentId.requestFocus();
            return;
        }

        if (!databaseHelper.studentExists(studentId)) {
            Toast.makeText(this,
                    "Student ID not found. Register the student first.",
                    Toast.LENGTH_SHORT).show();
            clearMedicalFields();
            return;
        }

        try (Cursor cursor = databaseHelper.getMedicalRecord(studentId)) {
            if (cursor.moveToFirst()) {
                etAllergies.setText(getValue(cursor, "allergies"));
                etConditions.setText(getValue(cursor, "medical_conditions"));
                etMedications.setText(getValue(cursor, "medications"));
                etMedicalNotes.setText(getValue(cursor, "notes"));

                Toast.makeText(this,
                        "Medical record loaded.",
                        Toast.LENGTH_SHORT).show();
            } else {
                clearMedicalFields();

                Toast.makeText(this,
                        "No medical record yet. You can enter one.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveMedicalRecord() {
        String studentId = etStudentId.getText().toString().trim();
        String allergies = etAllergies.getText().toString().trim();
        String conditions = etConditions.getText().toString().trim();
        String medications = etMedications.getText().toString().trim();
        String notes = etMedicalNotes.getText().toString().trim();

        if (studentId.isEmpty()) {
            etStudentId.setError("Enter a student ID");
            etStudentId.requestFocus();
            return;
        }

        if (!databaseHelper.studentExists(studentId)) {
            Toast.makeText(this,
                    "Student ID not found. Register the student first.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean saved = databaseHelper.saveMedicalRecord(
                studentId,
                allergies,
                conditions,
                medications,
                notes
        );

        if (saved) {
            Toast.makeText(this,
                    "Medical record saved successfully.",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,
                    "Unable to save medical record.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String getValue(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);

        if (index < 0 || cursor.isNull(index)) {
            return "";
        }

        String value = cursor.getString(index);
        return value == null ? "" : value;
    }

    private void clearMedicalFields() {
        etAllergies.setText("");
        etConditions.setText("");
        etMedications.setText("");
        etMedicalNotes.setText("");
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}
