package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AccountTypeActivity extends AppCompatActivity {
    private final int navy = Color.rgb(11, 35, 72);
    private final int blue = Color.rgb(52, 120, 200);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(navy);
        getWindow().setNavigationBarColor(navy);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(24), dp(24), dp(24), dp(24));
        root.setBackgroundColor(navy);

        TextView title = new TextView(this);
        title.setText("MCSC\nSCHOOL CLINIC");
        title.setTextColor(Color.WHITE);
        title.setTextSize(27);
        title.setTypeface(Typeface.SERIF, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView subtitle = new TextView(this);
        subtitle.setText("Choose your account type to continue");
        subtitle.setTextColor(Color.rgb(201, 221, 245));
        subtitle.setTextSize(14);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(-1, -2);
        subtitleParams.setMargins(0, dp(12), 0, dp(26));
        root.addView(subtitle, subtitleParams);

        Button student = makeButton("STUDENT", blue, Color.WHITE);
        Button admin = makeButton("ADMIN", Color.WHITE, navy);
        root.addView(student, buttonParams());
        root.addView(admin, buttonParams());

        // This is the entry screen, so Back exits the app rather than returning to another page.
        Button back = makeButton("Back", Color.rgb(22, 59, 112), Color.WHITE);
        LinearLayout.LayoutParams backParams = buttonParams();
        backParams.topMargin = dp(18);
        root.addView(back, backParams);

        student.setOnClickListener(v -> openAuth("STUDENT"));
        admin.setOnClickListener(v -> openAuth("ADMIN"));
        back.setOnClickListener(v -> finish());
        setContentView(root);
    }

    private Button makeButton(String value, int background, int foreground) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextSize(16);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setTextColor(foreground);
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setMinHeight(0);
        button.setMinimumHeight(0);
        button.setPadding(dp(16), dp(10), dp(16), dp(10));
        button.setBackground(rounded(background, dp(8)));
        button.setStateListAnimator(null);
        return button;
    }

    private LinearLayout.LayoutParams buttonParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(52));
        params.setMargins(0, dp(7), 0, dp(7));
        return params;
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void openAuth(String role) {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.putExtra("role", role);
        startActivity(intent);
    }
}
