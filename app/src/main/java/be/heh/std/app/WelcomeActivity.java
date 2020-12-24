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
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import java.util.Objects;

import be.heh.std.app.databinding.ActivityWelcomeBinding;
import be.heh.std.model.database.AppDatabase;
import be.heh.std.model.database.User;

public class WelcomeActivity extends Activity {

    private LinearLayout welcome_layout;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWelcomeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
        intent = getIntent();
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        User user = db.userdao().getUserById(intent.getIntExtra("user_id", 0));
        binding.setUser(user);


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
                //Intent test = new Intent(this, ChgPasswordActivity.class);
                //test.putExtras(intent.getExtras());
                intent.setClass(this, ChgPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.user_disconnect:
                startActivity(new Intent(this, LoginActivity.class));
                Toast.makeText(getApplicationContext(), getString(R.string.user_disconnected),
                        Toast.LENGTH_LONG).show();
                finish();
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