package com.example.myapplication;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisteredUsersActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private LinearLayout list;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String role = getSharedPreferences("MCSC_SESSION", MODE_PRIVATE).getString("role", "");
        if (!"ADMIN".equals(role)) { finish(); return; }
        getWindow().setStatusBarColor(Color.BLACK); getWindow().setNavigationBarColor(Color.BLACK);
        db = new DatabaseHelper(this);
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18,20,18,18); root.setBackgroundColor(Color.BLACK);
        TextView title = new TextView(this); title.setText("REGISTERED USERS"); title.setTextColor(Color.WHITE); title.setTextSize(23); title.setTypeface(null,1);
        root.addView(title);
        TextView note = new TextView(this); note.setText("Account details are shown below. Passwords are never displayed."); note.setTextColor(Color.LTGRAY); note.setTextSize(13);
        root.addView(note);
        ScrollView scroll = new ScrollView(this); list = new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); list.setPadding(0,14,0,14); scroll.addView(list);
        root.addView(scroll, new LinearLayout.LayoutParams(-1,0,1));
        Button back = new Button(this); back.setText("Back to Dashboard"); back.setAllCaps(false); root.addView(back); back.setOnClickListener(v -> finish());
        setContentView(root); loadUsers();
    }
    private void loadUsers() {
        list.removeAllViews(); int count=0;
        try (Cursor c = db.getAllUserAccounts()) {
            while(c.moveToNext()) {
                count++;
                long id=c.getLong(c.getColumnIndexOrThrow("user_id"));
                String name=c.getString(c.getColumnIndexOrThrow("full_name"));
                String identifier=c.getString(c.getColumnIndexOrThrow("identifier"));
                String phone=c.getString(c.getColumnIndexOrThrow("recovery_phone"));
                String role=c.getString(c.getColumnIndexOrThrow("role"));
                String created=c.getString(c.getColumnIndexOrThrow("created_at"));
                LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(15,14,15,14); card.setBackgroundColor(Color.rgb(30,30,30));
                LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2); cp.setMargins(0,0,0,12); list.addView(card,cp);
                TextView info=new TextView(this); info.setText(name+"\n"+"Login: "+identifier+"\nRecovery phone: "+phone+"\nRole: "+role+"\nRegistered: "+created); info.setTextColor(Color.WHITE); info.setTextSize(14); card.addView(info);
                Button delete=new Button(this); delete.setText("Delete account"); delete.setAllCaps(false); delete.setTextColor(Color.WHITE); delete.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(150,35,45))); card.addView(delete);
                long currentUserId = getSharedPreferences("MCSC_SESSION", MODE_PRIVATE).getLong("user_id", -1);
                if (id == currentUserId) {
                    delete.setEnabled(false);
                    delete.setText("Current account");
                }
                delete.setOnClickListener(v -> new android.app.AlertDialog.Builder(this).setTitle("Delete account?").setMessage("Delete "+name+"'s login account? This does not delete the student's clinic records.").setNegativeButton("Cancel",null).setPositiveButton("Delete",(d,w)->{
                    if(db.deleteUserAccount(id)) { Toast.makeText(this,"Account deleted.",Toast.LENGTH_SHORT).show(); loadUsers(); }
                    else Toast.makeText(this,"Cannot delete the last administrator account.",Toast.LENGTH_LONG).show();
                }).show());
            }
        }
        if(count==0){ TextView empty=new TextView(this); empty.setText("No registered users yet."); empty.setTextColor(Color.WHITE); empty.setGravity(Gravity.CENTER); list.addView(empty); }
    }
    @Override protected void onDestroy(){if(db!=null)db.close();super.onDestroy();}
}
