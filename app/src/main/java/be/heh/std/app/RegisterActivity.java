package be.heh.std.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Arrays;

public class RegisterActivity extends FormActivity {

    private boolean firstTime;
    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private EditText password;
    private EditText password2;

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

        notEmptyInputs.addAll(Arrays.asList(firstname, lastname, email, password, password2));

        form.setVisibility(View.VISIBLE);
        if(firstTime) info_msg.setText(getString(R.string.need_register));
    }

    @Override
    public void checkForm() {
        try {
            super.checkForm();

            verifyLength(firstname, 6, 20);
            verifyLength(lastname, 6, 20);
            verifyEmail(email);
            verifyPassword(password);
            checkMatch(password, password2);

        } catch (Exception e) {
            error_msg.setText(e.getMessage());
            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}