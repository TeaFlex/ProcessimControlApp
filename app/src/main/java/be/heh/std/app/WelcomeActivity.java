package be.heh.std.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Objects;

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
            info.setText(key+": ");
            TextView data = new TextView(this);
            data.setText(intent.getStringExtra("user_"+key));
            tr.addView(info);
            tr.addView(data);
            table.addView(tr);
        }

        if(Objects.equals(intent.getStringExtra("user_role"), "ADMIN")){
            Button manage_user = (Button) findViewById(R.id.user_management);
            manage_user.setVisibility(View.VISIBLE);
        }
    }

    public void onWelcomeClickManager(View v) {
        switch (v.getId()) {
            case R.id.modify_profile:
                intent.setClass(this, ModProfileActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.chg_password:
                intent.setClass(this, ChgPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.connect_to_plc:
                //TODO
                break;
            case R.id.user_management:
                //TODO
                break;
            default:
                break;
        }
    }
}