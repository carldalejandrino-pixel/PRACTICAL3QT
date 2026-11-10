package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private String role;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        role = getSharedPreferences("MCSC_SESSION", MODE_PRIVATE).getString("role", "");
        if (!role.equals("ADMIN") && !role.equals("STUDENT")) {
            Intent i = new Intent(this, AccountTypeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return;
        }

        boolean admin = role.equals("ADMIN");
        setContentView(admin ? R.layout.activity_main : R.layout.activity_student_dashboard);

        Button identification = findViewById(R.id.btnStudentIdentification);
        Button register = findViewById(R.id.btnRegisterStudent);
        Button medical = findViewById(R.id.btnManageMedicalRecord);
        Button visit = findViewById(R.id.btnRecordClinicVisit);
        Button viewRecords = findViewById(R.id.btnViewStudentRecord);
        Button users = findViewById(R.id.btnRegisteredUsers);
        Button logout = findViewById(R.id.btnLogout);

        identification.setOnClickListener(v -> open(StudentIdentificationActivity.class));
        register.setOnClickListener(v -> open(RegisterStudentActivity.class));
        medical.setOnClickListener(v -> open(ManageMedicalRecordActivity.class));
        visit.setOnClickListener(v -> open(RecordClinicVisitActivity.class));
        if (admin && viewRecords != null) viewRecords.setOnClickListener(v -> open(ViewStudentsActivity.class));
        if (admin && users != null) users.setOnClickListener(v -> open(RegisteredUsersActivity.class));
        if (logout != null) logout.setOnClickListener(v -> {
            getSharedPreferences("MCSC_SESSION", MODE_PRIVATE).edit().clear().apply();
            Intent i = new Intent(this, AccountTypeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }

    private void open(Class<?> activity) { startActivity(new Intent(this, activity)); }
}
