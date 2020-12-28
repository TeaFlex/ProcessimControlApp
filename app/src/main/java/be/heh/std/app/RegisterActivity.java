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
import be.heh.std.model.database.Role;

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
        super.onFormClickManager(v);
        switch (v.getId()) {
            case R.id.to_login:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void checkForm() throws Exception{
        super.checkForm();

        verifyLength(firstname, 4, 20);
        verifyLength(lastname, 4, 20);
        verifyEmail(email);
        verifyPassword(password);
        areTextMatching(password, password2);

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        User newUser = new User();
        newUser.firstname = firstname.getText().toString();
        newUser.lastname = lastname.getText().toString();
        newUser.email = email.getText().toString().toLowerCase();
        newUser.password = getHashedPassword(password.getText().toString());
        newUser.role = (firstTime)? Role.ADMIN : Role.BASIC;

        if(db.userdao().getUserByEmail(newUser.email) != null)
            throw new Exception(getString(R.string.taken_email_err));

        db.userdao().addUser(newUser);

        toastMessage(getString(R.string.register_success));
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}