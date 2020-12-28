package be.heh.std.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import be.heh.std.app.R;
import be.heh.std.app.activities.forms.ChgPasswordActivity;
import be.heh.std.app.activities.forms.LoginActivity;
import be.heh.std.app.activities.forms.ModProfileActivity;
import be.heh.std.app.databinding.ActivityWelcomeBinding;
import be.heh.std.model.database.AppDatabase;
import be.heh.std.model.database.User;

public class WelcomeActivity extends AppCompatActivity {

    private Intent intent;
    private User user;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        refreshContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshContent();
    }

    public void onWelcomeClickManager(View v) {
        Intent temp;
        switch (v.getId()) {
            case R.id.modify_profile:
                temp = new Intent(this, ModProfileActivity.class);
                temp.putExtras(intent);
                startActivity(temp);
                break;
            case R.id.chg_password:
                temp = new Intent(this, ChgPasswordActivity.class);
                temp.putExtras(intent);
                startActivity(temp);
                break;
            case R.id.user_disconnect:
                startActivity(new Intent(this, LoginActivity.class));
                Toast.makeText(getApplicationContext(), getString(R.string.user_disconnected),
                        Toast.LENGTH_LONG).show();
                finish();
                break;
            case R.id.user_management:
                temp = new Intent(this,  AdminUserActivity.class);
                temp.putExtras(intent);
                startActivity(temp);
                //TODO
                break;
            case R.id.plc_management:
                temp = new Intent(this, PlcManagementActivity.class);
                temp.putExtras(intent);
                startActivity(temp);
                //TODO
                break;
            default:
                break;
        }
    }

    public void refreshContent() {
        ActivityWelcomeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        user = db.userdao().getUserById(intent.getIntExtra("user_id", 0));
        binding.setUser(user);
    }
}