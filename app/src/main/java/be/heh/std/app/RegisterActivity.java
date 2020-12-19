package be.heh.std.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import java.util.Arrays;

import be.heh.std.database.AppDatabase;
import be.heh.std.database.User;
import be.heh.std.model.Roles;

public class RegisterActivity extends FormActivity {

    private boolean firstTime;
    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private EditText password;
    private EditText password2;
    private Button to_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firstTime = getIntent().getBooleanExtra("first_time", false);
        form = findViewById(R.id.register_form);
        firstname = (EditText) findViewById(R.id.register_firstname);
        lastname = (EditText) findViewById(R.id.register_lastname);
        email = (EditText) findViewById(R.id.register_email);
        password = (EditText) findViewById(R.id.register_password);
        password2 = (EditText) findViewById(R.id.register_password2);
        submit = (Button) findViewById(R.id.register_connect);
        to_login = (Button) findViewById(R.id.to_login);

        notEmptyInputs.addAll(Arrays.asList(firstname, lastname, email, password, password2));

        form.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        if(firstTime) {
            info_msg.setText(getString(R.string.need_register));
            to_login.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFormClickManager(View v) {
        try {
            super.onFormClickManager(v);
            switch (v.getId()) {
                case R.id.to_login:
                    startActivity(new Intent(this, LoginActivity.class));
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

            verifyLength(firstname, 6, 20);
            verifyLength(lastname, 6, 20);
            verifyEmail(email);
            verifyPassword(password);
            checkMatch(password, password2);

            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            User newUser = new User();
            newUser.firstname = firstname.getText().toString();
            newUser.lastname = lastname.getText().toString();
            newUser.email = email.getText().toString().toLowerCase();
            newUser.password = getHashedPassword(password.getText().toString());
            newUser.role = (firstTime)? Roles.ADMIN.name() : Roles.BASIC.name();
            db.userdao().addUser(newUser);

            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } catch (Exception e) {
            error_msg.setText(e.getMessage());
            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}