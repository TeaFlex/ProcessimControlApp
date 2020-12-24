package be.heh.std.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import java.util.Arrays;

import be.heh.std.model.database.AppDatabase;
import be.heh.std.model.database.User;

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
        super.onFormClickManager(v);
        switch (v.getId()) {
            case R.id.to_register:
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void checkForm() throws Exception {
        super.checkForm();

        verifyEmail(email);

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        User u = db.userdao().getUserByEmail(email.getText().toString().toLowerCase());

        if(u == null)
            throw new Exception(getString(R.string.nonexistent_user_err));

        if(!isPotentialHash(password.getText().toString(), u.password))
            throw new Exception(getString(R.string.not_matching_password_err));

        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.putExtra("user_id", u.id);
        startActivity(intent);
        finish();
    }
}