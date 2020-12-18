package be.heh.std.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import be.heh.std.database.AppDatabase;

public class StartActivity extends AppCompatActivity {

    private Intent intent = new Intent(this, LoginActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        try {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            Boolean isDatabaseEmpty = (db.userdao().getCountOfUsers() == 0);

            if(isDatabaseEmpty) {
                intent = new Intent(this, RegisterActivity.class);
                intent.putExtra("first_time", true);
            }
            startActivity(intent);
            finish();

        } catch (Exception e) {
            TextView v = (TextView)findViewById(R.id.info_msg);
            ProgressBar p = (ProgressBar) findViewById(R.id.pg_loading);
            p.setVisibility(View.GONE);
            v.setText(e.getMessage());
            v.setVisibility(View.VISIBLE);
        }
    }
}