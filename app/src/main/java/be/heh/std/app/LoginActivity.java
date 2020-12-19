package be.heh.std.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.Arrays;

import be.heh.std.database.AppDatabase;
import be.heh.std.database.User;
import be.heh.std.security.PasswordAuthentication;
import io.reactivex.Single;

public class LoginActivity extends FormActivity {

    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        form = findViewById(R.id.login_form);
        email = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);
        submit = (Button) findViewById(R.id.login_connect);

        notEmptyInputs.addAll(Arrays.asList(email, password));
        
        form.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        info_msg.setText(getString(R.string.need_login));
    }

    @Override
    public void onFormClickManager(View v) {
        try {
            super.onFormClickManager(v);
            switch (v.getId()) {
                case R.id.to_register:
                    startActivity(new Intent(this, RegisterActivity.class));
                    finish();
                    break;
            }
        } catch (Exception e) {
            error_msg.setText(e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void checkForm() {
        try {
            super.checkForm();

            verifyEmail(email);

            PasswordAuthentication p = new PasswordAuthentication();
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            User u = db.userdao().getUserByEmail(email.getText().toString().toLowerCase());

            if(u == null)
                throw new Exception(getString(R.string.nonexistent_user_err));

            if(!p.authenticate(password.getText().toString().toCharArray(), u.password))
                throw new Exception(getString(R.string.bad_check_pass_err));

            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.putExtra("user_id", String.valueOf(u.id));
            intent.putExtra("user_firstname", u.firstname);
            intent.putExtra("user_lastname", u.lastname);
            intent.putExtra("user_email", u.email);
            intent.putExtra("user_role", u.role);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            error_msg.setText(e.getMessage());
            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}