package be.heh.std.app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Set;

import be.heh.std.database.User;

public class WelcomeActivity extends Activity {

    private LinearLayout welcome_layout;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        intent = getIntent();
        TextView greetings = (TextView) findViewById(R.id.user_greetings);
        greetings.setText(String.format(getString(R.string.greetings),
                intent.getStringExtra("user_firstname")));

        TableLayout table = (TableLayout) findViewById(R.id.user_infos);
        TableRow tr;
        TextView info;

        String[] keys = {"id", "firstname", "lastname", "email", "role"};
        for(String key : keys) {
            tr = new TableRow(this);
            info = new TextView(this);
            info.setText(key);
            TextView data = new TextView(this);
            data.setText(intent.getStringExtra("user_"+key));
            data.setBackgroundResource(R.color.grey_tran);
            info.setBackgroundResource(R.color.grey_tran);
            tr.addView(info);
            tr.addView(data);
            table.addView(tr);
        }

    }

    public void onWelcomeClickManager(View v) {
        switch (v.getId()) {
            case R.id.modify_profile:
                break;
            case R.id.chg_password:
                break;
            default:
                break;
        }
    }
}