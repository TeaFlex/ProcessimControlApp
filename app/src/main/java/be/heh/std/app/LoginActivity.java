package be.heh.std.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends FormActivity {

    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        form = findViewById(R.id.login_form);
        //notEmptyInputs.put(getString(R.string.email), (EditText) findViewById(R.id.login_email));
        //notEmptyInputs.put(getString(R.string.password), (EditText) findViewById(R.id.login_password));
        email = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);
        submit = (Button) findViewById(R.id.login_connect);

        form.setVisibility(View.VISIBLE);
        info_msg.setText(getString(R.string.need_login));
    }

    @Override
    public void checkForm() {
        try {
            super.checkForm();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}