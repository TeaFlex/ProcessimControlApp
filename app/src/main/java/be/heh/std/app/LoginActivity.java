package be.heh.std.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;

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
        info_msg.setText(getString(R.string.need_login));
    }

    @Override
    public void checkForm() {
        try {
            super.checkForm();
        } catch (Exception e) {
            error_msg.setText(e.getMessage());
            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}