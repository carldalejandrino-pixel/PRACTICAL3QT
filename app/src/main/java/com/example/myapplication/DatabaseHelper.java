
package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "school_clinic.db";
    private static final int DATABASE_VERSION = 7;

    private static final String TABLE_STUDENTS = "students";
    private static final String TABLE_MEDICAL_RECORDS = "medical_records";
    private static final String TABLE_CLINIC_VISITS = "clinic_visits";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // =====================================================
    // DATABASE CREATION
    // =====================================================

    @Override
    public void onCreate(SQLiteDatabase db) {

        createStudentsTable(db);
        createMedicalRecordsTable(db);
        createClinicVisitsTable(db);
        createUserAccountsTable(db);
    }

    private void createStudentsTable(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS students (" +
                        "student_id TEXT PRIMARY KEY, " +
                        "full_name TEXT, " +
                        "grade_section TEXT, " +
                        "first_name TEXT, " +
                        "last_name TEXT NOT NULL DEFAULT '', " +
                        "grade TEXT, " +
                        "section TEXT, " +
                        "gender TEXT, " +
                        "age TEXT, " +
                        "contact TEXT)"
        );
    }

    private void createMedicalRecordsTable(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS medical_records (" +
                        "student_id TEXT PRIMARY KEY, " +
                        "allergies TEXT, " +
                        "medical_conditions TEXT, " +
                        "medications TEXT, " +
                        "blood_type TEXT, " +
                        "notes TEXT)"
        );
    }

    private void createClinicVisitsTable(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS clinic_visits (" +
                        "visit_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "student_id TEXT, " +
                        "visit_date TEXT, " +
                        "visit_time TEXT DEFAULT '', " +
                        "symptoms TEXT, " +
                        "temperature TEXT, " +
                        "treatment TEXT, " +
                        "notes TEXT)"
        );
    }

    // =====================================================
    // DATABASE UPGRADES
    // =====================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        db.beginTransaction();

        try {

            // Create missing tables without deleting existing data.
            createStudentsTable(db);
            createMedicalRecordsTable(db);
            createClinicVisitsTable(db);

            // Ensure all student columns exist.
            addColumnIfMissing(db, TABLE_STUDENTS,
                    "full_name", "TEXT");

            addColumnIfMissing(db, TABLE_STUDENTS,
                    "grade_section", "TEXT");

            addColumnIfMissing(db, TABLE_STUDENTS,
                    "first_name", "TEXT");

            addColumnIfMissing(db, TABLE_STUDENTS,
                    "last_name", "TEXT NOT NULL DEFAULT ''");

            addColumnIfMissing(db, TABLE_STUDENTS,
                    "grade", "TEXT");

            addColumnIfMissing(db, TABLE_STUDENTS,
                    "section", "TEXT");

            addColumnIfMissing(db, TABLE_STUDENTS,
                    "gender", "TEXT");

            addColumnIfMissing(db, TABLE_STUDENTS,
                    "age", "TEXT");

            addColumnIfMissing(db, TABLE_STUDENTS,
                    "contact", "TEXT");

            // Ensure all medical record columns exist.
            addColumnIfMissing(db, TABLE_MEDICAL_RECORDS,
                    "allergies", "TEXT");

            addColumnIfMissing(db, TABLE_MEDICAL_RECORDS,
                    "medical_conditions", "TEXT");

            addColumnIfMissing(db, TABLE_MEDICAL_RECORDS,
                    "medications", "TEXT");

            addColumnIfMissing(db, TABLE_MEDICAL_RECORDS,
                    "blood_type", "TEXT");

            addColumnIfMissing(db, TABLE_MEDICAL_RECORDS,
                    "notes", "TEXT");

            // Ensure all clinic visit columns exist.
            addColumnIfMissing(db, TABLE_CLINIC_VISITS,
                    "student_id", "TEXT");

            addColumnIfMissing(db, TABLE_CLINIC_VISITS,
                    "visit_date", "TEXT");

            addColumnIfMissing(db, TABLE_CLINIC_VISITS,
                    "visit_time", "TEXT DEFAULT ''");

            addColumnIfMissing(db, TABLE_CLINIC_VISITS,
                    "symptoms", "TEXT");

            addColumnIfMissing(db, TABLE_CLINIC_VISITS,
                    "temperature", "TEXT");

            addColumnIfMissing(db, TABLE_CLINIC_VISITS,
                    "treatment", "TEXT");

            addColumnIfMissing(db, TABLE_CLINIC_VISITS,
                    "notes", "TEXT");

            // Local login accounts are stored in this same SQLite database.
            createUserAccountsTable(db);

            // Migrate existing student names.
            migrateStudentNames(db);

            // Preserve existing grade information.
            migrateGradeInformation(db);

            db.setTransactionSuccessful();

        } catch (Exception e) {

            Log.e(TAG, "Database upgrade failed", e);
            throw new RuntimeException("Database upgrade failed", e);

        } finally {

            db.endTransaction();
        }
    }

    // =====================================================
    // COLUMN CHECKING
    // =====================================================

    private boolean columnExists(
            SQLiteDatabase db,
            String tableName,
            String columnName) {

        try (Cursor cursor = db.rawQuery(
                "PRAGMA table_info(" + tableName + ")",
                null)) {

            while (cursor.moveToNext()) {

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name"));

                if (columnName.equals(name)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void addColumnIfMissing(
            SQLiteDatabase db,
            String tableName,
            String columnName,
            String definition) {

        if (!columnExists(db, tableName, columnName)) {

            db.execSQL(
                    "ALTER TABLE " + tableName +
                            " ADD COLUMN " + columnName +
                            " " + definition
            );
        }
    }

    // =====================================================
    // STUDENT DATA MIGRATION
    // =====================================================

    private void migrateStudentNames(SQLiteDatabase db) {

        String sql =
                "SELECT student_id, first_name, last_name, full_name " +
                        "FROM students";

        try (Cursor cursor = db.rawQuery(sql, null)) {

            while (cursor.moveToNext()) {

                String studentId = cursor.getString(0);
                String firstName = cursor.getString(1);
                String lastName = cursor.getString(2);
                String fullName = cursor.getString(3);

                if (fullName == null || fullName.trim().isEmpty()) {
                    continue;
                }

                ContentValues values = new ContentValues();

                boolean update = false;

                if (firstName == null || firstName.trim().isEmpty()) {

                    String[] parts = fullName.trim().split("\\s+", 2);

                    values.put("first_name", parts[0]);

                    if (parts.length > 1) {
                        values.put("last_name", parts[1]);
                    }

                    update = true;

                } else if (lastName == null || lastName.trim().isEmpty()) {

                    String trimmedFullName = fullName.trim();
                    String trimmedFirstName = firstName.trim();

                    if (trimmedFullName.startsWith(trimmedFirstName)) {

                        String remaining = trimmedFullName
                                .substring(trimmedFirstName.length())
                                .trim();

                        if (!remaining.isEmpty()) {
                            values.put("last_name", remaining);
                            update = true;
                        }
                    }
                }

                if (update) {

                    db.update(
                            TABLE_STUDENTS,
                            values,
                            "student_id = ?",
                            new String[]{studentId}
                    );
                }
            }
        }
    }

    private void migrateGradeInformation(SQLiteDatabase db) {

        // Preserve old combined grade/section values.
        // Do not guess where the grade ends and section begins.

        db.execSQL(
                "UPDATE students " +
                        "SET grade = grade_section " +
                        "WHERE (grade IS NULL OR grade = '') " +
                        "AND grade_section IS NOT NULL " +
                        "AND grade_section != ''"
        );
    }

    // =====================================================
    // ADD STUDENT
    // =====================================================

    public boolean addStudent(
            String studentId,
            String firstName,
            String lastName,
            String grade,
            String section,
            String gender,
            String age,
            String contact) {

        if (studentId == null || studentId.trim().isEmpty()) {
            return false;
        }

        String cleanFirstName = firstName == null ? "" : firstName.trim();
        String cleanLastName = lastName == null ? "" : lastName.trim();

        String fullName = (cleanFirstName + " " + cleanLastName).trim();

        String gradeValue = grade == null ? "" : grade.trim();
        String sectionValue = section == null ? "" : section.trim();

        ContentValues values = new ContentValues();

        values.put("student_id", studentId.trim());

        values.put("first_name", cleanFirstName);
        values.put("last_name", cleanLastName);
        values.put("full_name", fullName);

        values.put("grade", gradeValue);
        values.put("section", sectionValue);

        values.put(
                "grade_section",
                (gradeValue + " " + sectionValue).trim()
        );

        values.put("gender", gender);
        values.put("age", age);
        values.put("contact", contact);

        try {

            long result = getWritableDatabase().insertOrThrow(
                    TABLE_STUDENTS,
                    null,
                    values
            );

            return result != -1;

        } catch (android.database.sqlite.SQLiteException e) {

            Log.e(TAG, "Could not register student", e);
            throw e;
        }
    }

    // Legacy method for older activities.
    public boolean addStudent(
            String studentId,
            String studentName,
            int age,
            String grade,
            String section) {

        if (studentName == null || studentName.trim().isEmpty()) {
            return false;
        }

        String[] parts = studentName.trim().split("\\s+", 2);

        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : "";

        return addStudent(
                studentId,
                firstName,
                lastName,
                grade,
                section,
                "",
                String.valueOf(age),
                ""
        );
    }

    // =====================================================
    // CHECK STUDENT
    // =====================================================

    public boolean studentExists(String studentId) {

        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT student_id FROM students WHERE student_id = ?",
                new String[]{studentId})) {

            return cursor.moveToFirst();
        }
    }

    // =====================================================
    // GET ALL STUDENTS
    // =====================================================

    public Cursor getAllStudents() {

        return getReadableDatabase().rawQuery(
                "SELECT * FROM students " +
                        "ORDER BY last_name ASC, first_name ASC",
                null
        );
    }

    // =====================================================
    // MEDICAL RECORDS
    // =====================================================

    public Cursor getMedicalRecord(String studentId) {

        return getReadableDatabase().rawQuery(
                "SELECT * FROM medical_records WHERE student_id = ?",
                new String[]{studentId}
        );
    }

    public boolean saveMedicalRecord(
            String studentId,
            String allergies,
            String conditions,
            String medications,
            String bloodType,
            String notes) {

        ContentValues values = new ContentValues();

        values.put("student_id", studentId);
        values.put("allergies", allergies);
        values.put("medical_conditions", conditions);
        values.put("medications", medications);
        values.put("blood_type", bloodType);
        values.put("notes", notes);

        return getWritableDatabase().insertWithOnConflict(
                TABLE_MEDICAL_RECORDS,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        ) != -1;
    }

    // Legacy method for older activities.
    public boolean saveMedicalRecord(
            String studentId,
            String allergies,
            String conditions,
            String medications,
            String notes) {

        return saveMedicalRecord(
                studentId,
                allergies,
                conditions,
                medications,
                "",
                notes
        );
    }

    // =====================================================
    // CLINIC VISITS
    // =====================================================

    public boolean addClinicVisit(
            String studentId,
            String date,
            String time,
            String symptoms,
            String temperature,
            String treatment,
            String notes) {

        ContentValues values = new ContentValues();

        values.put("student_id", studentId);
        values.put("visit_date", date);
        values.put("visit_time", time);
        values.put("symptoms", symptoms);
        values.put("temperature", temperature);
        values.put("treatment", treatment);
        values.put("notes", notes);

        return getWritableDatabase().insert(
                TABLE_CLINIC_VISITS,
                null,
                values
        ) != -1;
    }

    public Cursor getClinicVisits(String studentId) {

        return getReadableDatabase().rawQuery(
                "SELECT * FROM clinic_visits " +
                        "WHERE student_id = ? " +
                        "ORDER BY visit_id DESC",
                new String[]{studentId}
        );
    }

    public Cursor getAllClinicVisits() {

        return getReadableDatabase().rawQuery(
                "SELECT * FROM clinic_visits " +
                        "ORDER BY visit_id DESC",
                null
        );
    }

    // =====================================================
    // LOCAL USER ACCOUNTS
    // =====================================================

    private static final String TABLE_USER_ACCOUNTS = "user_accounts";

    private void createUserAccountsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS user_accounts (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name TEXT NOT NULL, " +
                "identifier TEXT NOT NULL COLLATE NOCASE UNIQUE, " +
                "recovery_phone TEXT NOT NULL, " +
                "role TEXT NOT NULL CHECK(role IN ('ADMIN','STUDENT')), " +
                "password_salt TEXT NOT NULL, " +
                "password_hash TEXT NOT NULL, " +
                "created_at TEXT NOT NULL)");
    }

    public int getAdminCount() {
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM user_accounts WHERE role='ADMIN'", null)) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    public boolean registerUser(String fullName, String identifier,
                                String recoveryPhone, String role, String password) {
        if (fullName == null || identifier == null || recoveryPhone == null ||
                role == null || password == null) return false;

        String cleanRole = role.trim().toUpperCase(java.util.Locale.ROOT);
        if (!cleanRole.equals("ADMIN") && !cleanRole.equals("STUDENT")) return false;
        if (cleanRole.equals("ADMIN") && getAdminCount() > 0) return false;

        byte[] salt = new byte[16];
        new java.security.SecureRandom().nextBytes(salt);
        String saltText = android.util.Base64.encodeToString(salt, android.util.Base64.NO_WRAP);
        String hashText = hashPassword(password, salt);

        ContentValues values = new ContentValues();
        values.put("full_name", fullName.trim());
        values.put("identifier", identifier.trim().toLowerCase(java.util.Locale.ROOT));
        values.put("recovery_phone", recoveryPhone.trim());
        values.put("role", cleanRole);
        values.put("password_salt", saltText);
        values.put("password_hash", hashText);
        values.put("created_at", new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                .format(new java.util.Date()));

        try {
            return getWritableDatabase().insertOrThrow(
                    TABLE_USER_ACCOUNTS, null, values) != -1;
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            return false;
        }
    }

    public Cursor authenticateUser(String identifier, String password, String role) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT user_id, full_name, identifier, role, password_salt, password_hash " +
                        "FROM user_accounts WHERE identifier=? AND role=? COLLATE NOCASE",
                new String[]{identifier.trim().toLowerCase(java.util.Locale.ROOT), role});
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        String saltText = cursor.getString(cursor.getColumnIndexOrThrow("password_salt"));
        String expected = cursor.getString(cursor.getColumnIndexOrThrow("password_hash"));
        String actual = hashPassword(password,
                android.util.Base64.decode(saltText, android.util.Base64.NO_WRAP));
        if (!constantTimeEquals(expected, actual)) {
            cursor.close();
            return null;
        }
        // Return a detached, safe cursor containing no password material.
        android.database.MatrixCursor result = new android.database.MatrixCursor(
                new String[]{"user_id", "full_name", "identifier", "role"});
        result.addRow(new Object[]{
                cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                cursor.getString(cursor.getColumnIndexOrThrow("identifier")),
                cursor.getString(cursor.getColumnIndexOrThrow("role"))});
        cursor.close();
        return result;
    }

    public boolean resetUserPassword(String identifier, String recoveryPhone, String newPassword) {
        if (identifier == null || recoveryPhone == null || newPassword == null) return false;
        byte[] salt = new byte[16];
        new java.security.SecureRandom().nextBytes(salt);
        ContentValues values = new ContentValues();
        values.put("password_salt", android.util.Base64.encodeToString(salt, android.util.Base64.NO_WRAP));
        values.put("password_hash", hashPassword(newPassword, salt));
        return getWritableDatabase().update(TABLE_USER_ACCOUNTS, values,
                "identifier=? AND recovery_phone=?",
                new String[]{identifier.trim().toLowerCase(java.util.Locale.ROOT), recoveryPhone.trim()}) == 1;
    }

    public Cursor getAllUserAccounts() {
        return getReadableDatabase().rawQuery(
                "SELECT user_id, full_name, identifier, recovery_phone, role, created_at " +
                        "FROM user_accounts ORDER BY created_at DESC", null);
    }

    public boolean deleteUserAccount(long userId) {
        // Keep at least one administrator so the app cannot become permanently locked.
        SQLiteDatabase db = getWritableDatabase();
        try (Cursor c = db.rawQuery(
                "SELECT role FROM user_accounts WHERE user_id=?",
                new String[]{String.valueOf(userId)})) {
            if (!c.moveToFirst()) return false;
            if ("ADMIN".equalsIgnoreCase(c.getString(0)) && getAdminCount() <= 1) {
                return false;
            }
        }
        return db.delete(TABLE_USER_ACCOUNTS, "user_id=?",
                new String[]{String.valueOf(userId)}) == 1;
    }

    private String hashPassword(String password, byte[] salt) {
        try {
            javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
                    password.toCharArray(), salt, 120000, 256);
            byte[] hash = javax.crypto.SecretKeyFactory.getInstance(
                    "PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            spec.clearPassword();
            return android.util.Base64.encodeToString(hash, android.util.Base64.NO_WRAP);
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException("Password hashing unavailable", e);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        return java.security.MessageDigest.isEqual(
                a.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                b.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

}
