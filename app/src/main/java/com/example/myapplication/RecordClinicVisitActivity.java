package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RecordClinicVisitActivity extends AppCompatActivity {

    private EditText etStudentId;
    private EditText etVisitDate;
    private EditText etVisitTime;
    private EditText etSymptoms;
    private EditText etTemperature;
    private EditText etTreatment;
    private EditText etVisitNotes;
    private TextView tvVisitHistory;

    private DatabaseHelper databaseHelper;
    private final Calendar selectedDateTime = Calendar.getInstance();

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat("hh:mm a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_clinic_visit);

        etStudentId = findViewById(R.id.etVisitStudentId);
        etVisitDate = findViewById(R.id.etVisitDate);
        etVisitTime = findViewById(R.id.etVisitTime);
        etSymptoms = findViewById(R.id.etSymptoms);
        etTemperature = findViewById(R.id.etTemperature);
        etTreatment = findViewById(R.id.etTreatment);
        etVisitNotes = findViewById(R.id.etVisitNotes);
        tvVisitHistory = findViewById(R.id.tvVisitHistory);

        Button btnLoadHistory = findViewById(R.id.btnLoadVisitHistory);
        Button btnSaveVisit = findViewById(R.id.btnSaveVisit);
        Button btnBack = findViewById(R.id.btnVisitBack);

        databaseHelper = new DatabaseHelper(this);

        etVisitDate.setFocusable(false);
        etVisitDate.setClickable(true);
        etVisitDate.setOnClickListener(v -> showDatePicker());

        etVisitTime.setFocusable(false);
        etVisitTime.setClickable(true);
        etVisitTime.setOnClickListener(v -> showTimePicker());

        updateDateAndTimeFields();

        btnLoadHistory.setOnClickListener(v -> loadVisitHistory());
        btnSaveVisit.setOnClickListener(v -> saveVisit());
        btnBack.setOnClickListener(v -> finish());
    }

    private void updateDateAndTimeFields() {
        etVisitDate.setText(dateFormat.format(selectedDateTime.getTime()));
        etVisitTime.setText(timeFormat.format(selectedDateTime.getTime()));
    }

    private void showDatePicker() {
        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateAndTimeFields();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showTimePicker() {
        new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    updateDateAndTimeFields();
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                false
        ).show();
    }

    private void saveVisit() {
        String studentId = etStudentId.getText().toString().trim();
        String visitDate = etVisitDate.getText().toString().trim();
        String visitTime = etVisitTime.getText().toString().trim();
        String symptoms = etSymptoms.getText().toString().trim();
        String temperature = etTemperature.getText().toString().trim();
        String treatment = etTreatment.getText().toString().trim();
        String notes = etVisitNotes.getText().toString().trim();

        if (studentId.isEmpty()) {
            etStudentId.setError("Enter a student ID");
            etStudentId.requestFocus();
            return;
        }

        if (visitDate.isEmpty()) {
            etVisitDate.setError("Select the visit date");
            return;
        }

        if (visitTime.isEmpty()) {
            etVisitTime.setError("Select the visit time");
            return;
        }

        if (!databaseHelper.studentExists(studentId)) {
            Toast.makeText(this,
                    "Student ID not found. Register the student first.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!temperature.isEmpty()) {
            try {
                float value = Float.parseFloat(temperature);

                if (!Float.isFinite(value) || value < 25 || value > 45) {
                    etTemperature.setError(
                            "Enter a valid temperature in °C");
                    etTemperature.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                etTemperature.setError(
                        "Enter temperature as a number, e.g. 36.5");
                etTemperature.requestFocus();
                return;
            }
        }

        boolean saved = databaseHelper.addClinicVisit(
                studentId, visitDate, visitTime,
                symptoms, temperature, treatment, notes);

        if (saved) {
            Toast.makeText(this, "Clinic visit saved successfully",
                    Toast.LENGTH_SHORT).show();

            etSymptoms.setText("");
            etTemperature.setText("");
            etTreatment.setText("");
            etVisitNotes.setText("");

            loadVisitHistory();
        } else {
            Toast.makeText(this, "Unable to save clinic visit",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void loadVisitHistory() {
        String studentId = etStudentId.getText().toString().trim();

        if (studentId.isEmpty()) {
            etStudentId.setError("Enter a student ID");
            etStudentId.requestFocus();
            return;
        }

        if (!databaseHelper.studentExists(studentId)) {
            Toast.makeText(this, "Student ID not found",
                    Toast.LENGTH_SHORT).show();
            tvVisitHistory.setText("No registered student found.");
            return;
        }

        StringBuilder history = new StringBuilder();

        try (Cursor cursor = databaseHelper.getClinicVisits(studentId)) {
            if (cursor.moveToFirst()) {
                do {
                    String date = cursor.getString(
                            cursor.getColumnIndexOrThrow("visit_date"));
                    String time = cursor.getString(
                            cursor.getColumnIndexOrThrow("visit_time"));
                    String symptoms = cursor.getString(
                            cursor.getColumnIndexOrThrow("symptoms"));
                    String temperature = cursor.getString(
                            cursor.getColumnIndexOrThrow("temperature"));
                    String treatment = cursor.getString(
                            cursor.getColumnIndexOrThrow("treatment"));
                    String notes = cursor.getString(
                            cursor.getColumnIndexOrThrow("notes"));

                    history.append("Date: ").append(showValue(date))
                            .append("\nTime: ")
                            .append(showValue(time))
                            .append("\nSymptoms: ").append(showValue(symptoms))
                            .append("\nTemperature: ")
                            .append(temperature == null || temperature.isEmpty()
                                    ? "Not recorded" : temperature + " °C")
                            .append("\nTreatment: ").append(showValue(treatment))
                            .append("\nNotes: ").append(showValue(notes))
                            .append("\n\n-------------------------\n\n");
                } while (cursor.moveToNext());

                tvVisitHistory.setText(history.toString());
            } else {
                tvVisitHistory.setText(
                        "No clinic visits recorded for this student.");
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
