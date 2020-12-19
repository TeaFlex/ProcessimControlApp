package be.heh.std.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Set;

import be.heh.std.database.User;

public class WelcomeActivity extends AppCompatActivity {

    private LinearLayout welcome_layout;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        intent = getIntent();
        TextView greetings = (TextView) findViewById(R.id.user_greetings);
        greetings.setText(String.format(getString(R.string.greetings), intent.getStringExtra("user_firstname")));

        TableLayout table = (TableLayout) findViewById(R.id.user_infos);
        TableRow tr;
        TextView info;


        Set<String> keyset = intent.getExtras().keySet();

        for(String name : keyset) {
            tr = new TableRow(this);
            info = new TextView(this);
            info.setText(name);
            TextView data = new TextView(this);
            data.setText(intent.getStringExtra(name));
            tr.addView(info);
            tr.addView(data);
            table.addView(tr);
        }

    }
}