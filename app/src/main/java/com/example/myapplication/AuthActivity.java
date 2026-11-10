package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private String role;
    private boolean signup = false;
    private LinearLayout fields;
    private EditText nameInput, identifierInput, phoneInput, passwordInput, confirmInput;
    private Button primaryButton, toggleButton, forgotButton;
    private TextView heading, helper;
    private int bg, card, accent, text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        role = getIntent().getStringExtra("role");
        if (!"ADMIN".equals(role) && !"STUDENT".equals(role)) role = "STUDENT";
        db = new DatabaseHelper(this);
        boolean admin = role.equals("ADMIN");
        bg = admin ? Color.BLACK : Color.rgb(11, 35, 72);
        card = admin ? Color.rgb(28, 28, 28) : Color.rgb(22, 59, 112);
        accent = admin ? Color.WHITE : Color.rgb(52, 120, 200);
        text = Color.WHITE;
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        buildScreen();
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(bg);
        scroll.setClipToPadding(false);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(14), dp(20), dp(24));
        root.setBackgroundColor(bg);
        scroll.addView(root, new ScrollView.LayoutParams(-1, -1));

        Button back = makeButton("‹  Back", card, text, false);
        LinearLayout.LayoutParams backParams = new LinearLayout.LayoutParams(-2, dp(42));
        backParams.bottomMargin = dp(18);
        root.addView(back, backParams);
        back.setOnClickListener(v -> finish());

        TextView brand = label("MCSC  |  " + role, 13, accent, true);
        root.addView(brand);

        heading = label("Login", 28, text, true);
        LinearLayout.LayoutParams headingParams = new LinearLayout.LayoutParams(-1, -2);
        headingParams.topMargin = dp(12);
        headingParams.bottomMargin = dp(4);
        root.addView(heading, headingParams);

        helper = label("Sign in using your registered email or phone number.", 13,
                role.equals("ADMIN") ? Color.LTGRAY : Color.rgb(201, 221, 245), false);
        root.addView(helper);

        fields = new LinearLayout(this);
        fields.setOrientation(LinearLayout.VERTICAL);
        fields.setPadding(dp(12), dp(14), dp(12), dp(2));
        fields.setBackground(rounded(card, dp(8), Color.TRANSPARENT, 0));
        LinearLayout.LayoutParams fieldsParams = new LinearLayout.LayoutParams(-1, -2);
        fieldsParams.topMargin = dp(16);
        fieldsParams.bottomMargin = dp(14);
        root.addView(fields, fieldsParams);

        nameInput = addField("Full name", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        identifierInput = addField("Gmail, Yahoo email, or phone number",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        phoneInput = addField("Recovery phone number", InputType.TYPE_CLASS_PHONE);
        passwordInput = addField("Password", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmInput = addField("Confirm password", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        primaryButton = makeButton("Login", accent, role.equals("ADMIN") ? Color.BLACK : Color.WHITE, true);
        root.addView(primaryButton, actionButtonParams());

        toggleButton = makeButton("Create an account", card, text, true);
        root.addView(toggleButton, actionButtonParams());

        forgotButton = makeButton("Forgot password?", bg, text, false);
        root.addView(forgotButton, actionButtonParams());

        primaryButton.setOnClickListener(v -> submit());
        toggleButton.setOnClickListener(v -> {
            signup = !signup;
            renderMode();
            scroll.smoothScrollTo(0, 0);
        });
        forgotButton.setOnClickListener(v -> showForgotPassword());

        renderMode();
        setContentView(scroll);
    }

    private EditText addField(String hint, int type) {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setHint(hint);
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.LTGRAY);
        input.setTextSize(14);
        input.setInputType(type);
        input.setGravity(Gravity.CENTER_VERTICAL);
        input.setPadding(dp(14), dp(8), dp(14), dp(8));
        int stroke = role.equals("ADMIN") ? Color.rgb(105, 105, 105) : Color.rgb(91, 153, 219);
        input.setBackground(rounded(role.equals("ADMIN") ? Color.rgb(42, 42, 42) : Color.rgb(17, 48, 91),
                dp(7), stroke, dp(1)));
        input.setSelectAllOnFocus(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(52));
        params.setMargins(0, 0, 0, dp(10));
        fields.addView(input, params);
        return input;
    }

    private void renderMode() {
        heading.setText(signup ? "Create account" : "Login");
        helper.setText(signup
                ? "Use a Gmail/Yahoo address or a phone number."
                : "Sign in using your registered email or phone number.");
        nameInput.setVisibility(signup ? View.VISIBLE : View.GONE);
        phoneInput.setVisibility(signup ? View.VISIBLE : View.GONE);
        confirmInput.setVisibility(signup ? View.VISIBLE : View.GONE);
        primaryButton.setText(signup ? "Sign Up" : "Login");
        toggleButton.setText(signup ? "Already have an account? Login" : "Create an account");
    }

    private void submit() {
        String identifier = identifierInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        if (!validIdentifier(identifier)) {
            identifierInput.setError("Please use Gmail or Yahoo, or use phone number.");
            return;
        }
        if (signup) {
            String name = nameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String confirm = confirmInput.getText().toString();
            if (name.isEmpty()) { nameInput.setError("Enter your full name"); return; }
            if (!validPhone(phone)) { phoneInput.setError("Enter a valid recovery phone number"); return; }
            if (password.length() < 8) { passwordInput.setError("Use at least 8 characters"); return; }
            if (!password.equals(confirm)) { confirmInput.setError("Passwords do not match"); return; }
            if (role.equals("ADMIN") && db.getAdminCount() > 0) {
                toast("An administrator account already exists. Ask the current administrator to manage access.");
                return;
            }
            boolean ok = db.registerUser(name, identifier, phone, role, password);
            if (ok) {
                toast("Account created. You can now log in.");
                signup = false;
                passwordInput.setText("");
                confirmInput.setText("");
                renderMode();
            } else {
                toast("An account with this email or phone number already exists.");
            }
        } else {
            if (password.isEmpty()) { passwordInput.setError("Enter your password"); return; }
            try (android.database.Cursor cursor = db.authenticateUser(identifier, password, role)) {
                if (cursor == null || !cursor.moveToFirst()) {
                    toast("Incorrect login details.");
                    return;
                }
                long userId = cursor.getLong(cursor.getColumnIndexOrThrow("user_id"));
                String fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                getSharedPreferences("MCSC_SESSION", MODE_PRIVATE).edit()
                        .putLong("user_id", userId)
                        .putString("role", role)
                        .putString("full_name", fullName)
                        .apply();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
    }

    private boolean validIdentifier(String value) {
        if (validPhone(value)) return true;
        if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) return false;
        String lower = value.toLowerCase(java.util.Locale.ROOT);
        return lower.endsWith("@gmail.com") || lower.endsWith("@yahoo.com")
                || lower.endsWith("@yahoo.com.ph") || lower.endsWith("@ymail.com");
    }

    private boolean validPhone(String value) {
        return value.matches("^\\+?[0-9]{10,15}$");
    }

    private void showForgotPassword() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(8), dp(4), dp(8), dp(4));
        EditText id = dialogField("Registered Gmail, Yahoo, or phone");
        EditText phone = dialogField("Recovery phone number");
        phone.setInputType(InputType.TYPE_CLASS_PHONE);
        EditText newPass = dialogField("New password (8+ characters)");
        newPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        box.addView(id);
        box.addView(phone);
        box.addView(newPass);
        new android.app.AlertDialog.Builder(this)
                .setTitle("Reset password")
                .setMessage("Offline reset checks the account identifier and recovery phone stored on this device. It does not send an email or SMS verification code.")
                .setView(box)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Reset", (dialog, which) -> {
                    String account = id.getText().toString().trim();
                    String recoveryPhone = phone.getText().toString().trim();
                    String newPassword = newPass.getText().toString();
                    if (!validIdentifier(account) || !validPhone(recoveryPhone) || newPassword.length() < 8) {
                        toast("Check the details. Password must be at least 8 characters.");
                        return;
                    }
                    if (db.resetUserPassword(account, recoveryPhone, newPassword))
                        toast("Password updated on this device.");
                    else toast("Account and recovery phone did not match.");
                })
                .show();
    }

    private EditText dialogField(String hint) {
        EditText field = new EditText(this);
        field.setHint(hint);
        field.setSingleLine(true);
        field.setTextColor(Color.WHITE);
        field.setHintTextColor(Color.LTGRAY);
        field.setPadding(dp(12), dp(8), dp(12), dp(8));
        field.setBackground(rounded(card, dp(6), Color.GRAY, dp(1)));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(48));
        params.bottomMargin = dp(8);
        field.setLayoutParams(params);
        return field;
    }

    private TextView label(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        if (bold) view.setTypeface(Typeface.SERIF, Typeface.BOLD);
        return view;
    }

    private Button makeButton(String value, int background, int foreground, boolean bold) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextColor(foreground);
        button.setTextSize(14);
        button.setTypeface(Typeface.DEFAULT, bold ? Typeface.BOLD : Typeface.NORMAL);
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setMinHeight(0);
        button.setMinimumHeight(0);
        button.setPadding(dp(12), dp(8), dp(12), dp(8));
        button.setBackground(rounded(background, dp(7),
                background == bg ? (role.equals("ADMIN") ? Color.DKGRAY : Color.rgb(65, 104, 155)) : Color.TRANSPARENT,
                background == bg ? dp(1) : 0));
        button.setStateListAnimator(null);
        return button;
    }

    private LinearLayout.LayoutParams actionButtonParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(48));
        params.setMargins(0, dp(5), 0, dp(5));
        return params;
    }

    private GradientDrawable rounded(int color, int radius, int strokeColor, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        if (strokeWidth > 0) drawable.setStroke(strokeWidth, strokeColor);
        return drawable;
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        if (db != null) db.close();
        super.onDestroy();
    }
}
